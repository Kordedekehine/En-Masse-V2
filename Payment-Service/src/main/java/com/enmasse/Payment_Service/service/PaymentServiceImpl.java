package com.enmasse.Payment_Service.service;

import com.enmasse.Payment_Service.client.UserClient;
import com.enmasse.Payment_Service.dtos.*;
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
    public CreatePaymentResponse createPayment(CreatePaymentRequest createPaymentRequest, HttpServletRequest request) {
        Stripe.apiKey = secretKey; // Set your Stripe secret key

        UserInfoResponse userInfos = extractUserIdFromRequest(request);

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
                                                    .setUnitAmount(createPaymentRequest.amount())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(userInfos.getName())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

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

        ResponseEntity<UserInfoResponse> userInfoResponse = userClient.getUserInfo(request);
        UserInfoResponse userInfo = userInfoResponse.getBody();

        return userInfo;
    }

}
