package com.enmasse.Payment_Service.controller;

import com.enmasse.Payment_Service.dtos.CreatePaymentRequest;
import com.enmasse.Payment_Service.dtos.StripeResponse;
import com.enmasse.Payment_Service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay")
public class PaymentController {

@Autowired
private PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<StripeResponse> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        StripeResponse stripeResponse = paymentService.createPayment(createPaymentRequest);
        return ResponseEntity
                .status(stripeResponse.httpStatus())
                .body(stripeResponse);
    }


    @GetMapping("/capture-payment")
    public ResponseEntity<StripeResponse> capturePayment(@RequestParam String sessionId) {
        StripeResponse stripeResponse = paymentService.capturePayment(sessionId);
        return ResponseEntity
                .status(stripeResponse.httpStatus())
                .body(stripeResponse);
    }
}
