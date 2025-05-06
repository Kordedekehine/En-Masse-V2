package com.enmasse.Order_Service.service;

import com.enmasse.Order_Service.client.PaymentClient;
import com.enmasse.Order_Service.client.ProductClient;
import com.enmasse.Order_Service.client.UserClient;
import com.enmasse.Order_Service.config.NotificationEventProducer;
import com.enmasse.Order_Service.config.OrderEventProducer;
import com.enmasse.Order_Service.config.StockEventProducer;
import com.enmasse.Order_Service.dtos.*;
import com.enmasse.Order_Service.entity.Order;
import com.enmasse.Order_Service.entity.OrderItem;
import com.enmasse.Order_Service.entity.OrderStatus;
import com.enmasse.Order_Service.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderEventProducer orderEventProducer;

    @Autowired
    private StockEventProducer stockEventProducer;

    @Autowired
    private NotificationEventProducer notificationEventProducer;


   @Override
    public OrderResponse placeOrder(OrderRequest orderRequest, HttpServletRequest request) {

       UserInfoResponse userInfos = extractUserIdFromRequest(request);
       String userId = userInfos.getSub();

        List<Long> productIds = orderRequest.items().stream()
                .map(OrderItemRequest::productId)
                .toList();

        List<ProductResponseDto> products = productClient.getProductsByIds(productIds);

        if (products.size() != productIds.size()) {
            throw new RuntimeException("Some products not found in catalog");
        }

        // 2. Validate stock and prepare order items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.items()) {
            ProductResponseDto product = products.stream()
                    .filter(p -> p.id().equals(itemRequest.productId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.productId()));

            if (product.stock() < itemRequest.quantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.name());
            }

            BigDecimal subTotal = product.price().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalAmount = totalAmount.add(subTotal);

            log.info("amount : " + totalAmount.toString());

            if (totalAmount.compareTo(BigDecimal.valueOf(1000)) < 0) {
                throw new IllegalArgumentException("Minimum amount must be at least ₦1000 due to Stripe limits");
            }

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.id())
                    .productName(product.name())
                    .price(product.price())
                    .quantity(itemRequest.quantity())
                    .build();

            orderItems.add(orderItem);
        }


       ResponseEntity<CreatePaymentResponse> paymentResponseEntity = paymentClient.createPayment(
               CreatePaymentRequest.builder()
                       .name("Order for user " + userInfos.getName())
                       .currency("ngn")
                       .amount(totalAmount)
                       .quantity((long) orderItems.size())
                       .successUrl("https://yourapp.com/payment-success")  // frontend builds a checkout page
                       .cancelUrl("https://yourapp.com/payment-cancel")
                       .build()
       );

       if (paymentResponseEntity == null || !paymentResponseEntity.getStatusCode().is2xxSuccessful()) {
           throw new RuntimeException("Payment session creation failed");
       }

       CreatePaymentResponse paymentResponse = paymentResponseEntity.getBody();

       if (paymentResponse == null) {
           throw new RuntimeException("Payment session creation returned no data");
       }

       String sessionUrl = paymentResponse.sessionUrl();
       String sessionId = paymentResponse.sessionId();

       Order order = orderRepository.save(Order.builder()
               .userId(userId)
               .orderNumber(UUID.randomUUID().toString())
               .items(orderItems)
               .status(OrderStatus.AWAITING_PAYMENT)
               .deliveryAddress(orderRequest.deliveryAddress())
               .created(Instant.now())
               .sessionId(sessionId)
               .sessionUrl(sessionUrl)
               .build()
       );

       orderEventProducer.sendOrderCreatedEvent(new OrderCreatedEvent(
                        userId,
                        orderItems.stream()
                                .map(item -> new OrderItemRequest(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getQuantity(),
                                        item.getPrice()
                                ))
                                .toList(), sessionId));

        List<ProductStockDto> productsOrdered = order.getItems().stream()
                .map(item -> new ProductStockDto(item.getProductId(), item.getQuantity()))
                .toList();

        stockEventProducer.sendStockUpdateEvent(productsOrdered);

        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .items(order.getItems().stream().map(this::mapOrderItemToResponse).toList())
                .totalCost(totalAmount)
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus().toString())
                .created(order.getCreated().toString())
                .sessionId(sessionId)
                .sessionUrl(sessionUrl)
                .build();

        log.info("Stripe session URL: {}", sessionUrl);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .recipientEmail(userInfos.getEmail())
                .message("Your order has been placed successfully! Kindly confirm your payment")
                .eventType("ORDER_PLACED")
                .build();

        notificationEventProducer.sendNotification(notificationEvent);

        return response;
    }

    @Transactional
    public CapturePaymentResponse confirmPaymentAndUpdateOrder(String sessionId) {

        ResponseEntity<CapturePaymentResponse> paymentResponseEntity = paymentClient.capturePayment(sessionId);
        CapturePaymentResponse paymentResponse = paymentResponseEntity.getBody();

        if (paymentResponse != null) {
            if ("complete".equalsIgnoreCase(paymentResponse.sessionStatus()) &&
                    "paid".equalsIgnoreCase(paymentResponse.paymentStatus())) {

                Order order = orderRepository.findBySessionId(sessionId)
                        .orElseThrow(() -> new RuntimeException("Order not found for sessionId: " + sessionId));

                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);

                log.info("Order {} payment confirmed and status updated to SUCCESS", order.getOrderNumber());
            } else {
                log.warn("Session {} payment not completed or not paid. SessionStatus: {}, PaymentStatus: {}",
                        sessionId, paymentResponse.sessionStatus(), paymentResponse.paymentStatus());
            }
        } else {
            log.error("Failed to capture payment for sessionId: {}.", sessionId);
        }

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .message("Your order has been placed successfully!")
                .eventType("ORDER_TO_BE_DELIVERED")
                .build();

        notificationEventProducer.sendNotification(notificationEvent);

        return paymentResponse;
    }


    private OrderResponse mapOrderToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getOrderNumber(),
                order.getItems().stream().map(this::mapOrderItemToResponse).toList(),
                order.getItems().stream().reduce(
                        BigDecimal.ZERO,
                        (subtotal, orderItem) -> subtotal.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))),
                        BigDecimal::add
                ),
                order.getDeliveryAddress(),
                order.getStatus().name(),
                order.getCreated().toString(),
                order.getSessionId(),
                order.getSessionUrl()
        );
    }

    private OrderItemResponse mapOrderItemToResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProductId(),
                orderItem.getProductName(),
                "/api/v1/product/" + orderItem.getProductId() + "/image", //to be modified
                orderItem.getPrice(),
                orderItem.getQuantity()
        );
    }

    private OrderItem mapRequestToOrderItem(OrderItemRequest request, String productName, BigDecimal unitPrice) {
        return OrderItem.builder()
                .productId(request.productId())
                .productName(productName)
                .price(unitPrice)
                .quantity(request.quantity())
                .build();
    }



    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
                .map(this::mapOrderToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderResponse> getOrdersByUser(String userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapOrderToResponse)
                .toList();
    }


    public UserInfoResponse extractUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }


        ResponseEntity<UserInfoResponse> userInfoResponse = userClient.getUserInfo(authHeader);
        UserInfoResponse userInfo = userInfoResponse.getBody();

        return userInfo;
    }

}
