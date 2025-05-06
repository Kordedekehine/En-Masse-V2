package com.enmasse.Payment_Service.controller;

import com.enmasse.Payment_Service.dtos.CapturePaymentResponse;
import com.enmasse.Payment_Service.dtos.CreatePaymentRequest;
import com.enmasse.Payment_Service.dtos.CreatePaymentResponse;
import com.enmasse.Payment_Service.dtos.StripeResponse;
import com.enmasse.Payment_Service.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay")
public class PaymentController {

@Autowired
private PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        CreatePaymentResponse createPaymentResponse = paymentService.createPayment(createPaymentRequest);
        return ResponseEntity.ok(createPaymentResponse);
    }

    @GetMapping("/capture-payment")
    public ResponseEntity<CapturePaymentResponse> capturePayment(@RequestParam String sessionId) {
        CapturePaymentResponse capturePaymentResponse = paymentService.capturePayment(sessionId);
        return ResponseEntity.ok(capturePaymentResponse);
    }

}
