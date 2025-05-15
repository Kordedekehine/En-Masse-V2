package com.enmasse.Payment_Service.service;

import com.enmasse.Payment_Service.client.UserClient;
import com.enmasse.Payment_Service.dtos.*;
import com.enmasse.Payment_Service.entity.Payment;
import com.enmasse.Payment_Service.repository.PaymentRepository;
import com.enmasse.Payment_Service.utils.Constant;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserClient userClient;

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
        Stripe.apiKey = secretKey; // Set your Stripe secret key

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(createPaymentRequest.successUrl())
                    .setCancelUrl(createPaymentRequest.cancelUrl())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(createPaymentRequest.quantity())
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(createPaymentRequest.currency())
                                                    .setUnitAmount(createPaymentRequest.amount().multiply(BigDecimal.valueOf(100))
                                                            .setScale(0, RoundingMode.HALF_UP).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(createPaymentRequest.name())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            Payment payment = new Payment();
            payment.setSessionId(session.getId());
            payment.setCurrency(createPaymentRequest.currency());
            payment.setAmount(createPaymentRequest.amount());
            payment.setCurrency(createPaymentRequest.currency());
            payment.setQuantity(createPaymentRequest.quantity());
            payment.setStatus(session.getStatus());

            paymentRepository.save(payment);

            return CreatePaymentResponse.builder()
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create Stripe payment session", e);
        }
    }


    @Override
    public CapturePaymentResponse capturePayment(String sessionId) {
        Stripe.apiKey = secretKey;

        try {
            Session session = Session.retrieve(sessionId);
            String status = session.getStatus();

            if ("complete".equalsIgnoreCase(status)) {
                log.info("Payment successfully captured for sessionId: {}", sessionId);
            } else {
                log.info("Payment not yet completed for sessionId: {}. Current status: {}", sessionId, status);
            }

            Payment payment = paymentRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new RuntimeException("Order not found for sessionId: " + sessionId));

            payment.setStatus(status);
            paymentRepository.save(payment);

            return CapturePaymentResponse.builder()
                    .sessionId(sessionId)
                    .sessionStatus(status)
                    .paymentStatus(session.getPaymentStatus())
                    .build();

        } catch (StripeException e) {
            log.error("Error capturing payment for sessionId {}: {}", sessionId, e.getMessage(), e);
            throw new RuntimeException("Failed to capture payment", e);
        }
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
