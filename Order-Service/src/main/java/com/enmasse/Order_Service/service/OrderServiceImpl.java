package com.enmasse.Order_Service.service;

import com.enmasse.Order_Service.config.OrderEventProducer;
import com.enmasse.Order_Service.config.PaymentClient;
import com.enmasse.Order_Service.config.ProductClient;
import com.enmasse.Order_Service.config.StockEventProducer;
import com.enmasse.Order_Service.dtos.*;
import com.enmasse.Order_Service.entity.Order;
import com.enmasse.Order_Service.entity.OrderItem;
import com.enmasse.Order_Service.entity.OrderStatus;
import com.enmasse.Order_Service.repository.OrderRepository;
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
    private OrderEventProducer orderEventProducer;

    @Autowired
    private StockEventProducer stockEventProducer;


   @Override
    public ResponseEntity<OrderResponse> placeOrder(OrderRequest orderRequest, Long userId) {
        // 1. Fetch product details
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

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.id())
                    .productName(product.name())
                    .price(product.price())
                    .quantity(itemRequest.quantity())
                    .build();

            orderItems.add(orderItem);
        }

        Order order = orderRepository.save(Order.builder()
                .userId(userId)
                .orderNumber(UUID.randomUUID().toString())
                .items(orderItems)
                .status(OrderStatus.AWAITING_PAYMENT)
                .deliveryAddress(orderRequest.deliveryAddress())
                .created(Instant.now())
                .build()
        );

        // 4. Create Stripe payment session
        StripeResponse<CreatePaymentResponse> paymentResponse = paymentClient.createPayment(
                CreatePaymentRequest.builder()
                        .name("Order for user " + userId)
                        .currency("ngn")
                        .amount(totalAmount.longValue())
                        .quantity((long) orderItems.size())
                        .successUrl("https://yourapp.com/payment-success") //frontend should build a checkoutpage
                        .cancelUrl("https://yourapp.com/payment-cancel")
                        .build()
        ).getBody();

        if (paymentResponse == null || !"SUCCESS".equalsIgnoreCase(paymentResponse.status())) {
            throw new RuntimeException("Payment session creation failed");
        }

        String sessionUrl = paymentResponse.data().sessionUrl();
        String sessionId = paymentResponse.data().sessionId();

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

        // 7. Build response
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
                .build();

        log.info("Stripe session URL: {}", sessionUrl);

        return ResponseEntity.ok(response);
    }


//    private List<OrderItemResponse> mapToItemResponses(List<OrderItem> items) {
//        return items.stream()
//                .map(item -> new OrderItemResponse(
//                        item.getProductId(),
//                        item.getProductName(),
//                        item.getPrice(),
//                        item.getQuantity()
//                ))
//                .toList();
//    }



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
                order.getSessionId()
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

    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapOrderToResponse)
                .toList();
    }

}
