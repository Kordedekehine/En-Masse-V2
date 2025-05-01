package com.enmasse.Order_Service.client;

import com.enmasse.Order_Service.dtos.CapturePaymentResponse;
import com.enmasse.Order_Service.dtos.CreatePaymentRequest;
import com.enmasse.Order_Service.dtos.CreatePaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${payment-service.name}",url = "${payment-service.url}")
public interface PaymentClient {

    @PostMapping("/create-payment")
    ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest);

    @GetMapping("/capture-payment")
    ResponseEntity<CapturePaymentResponse> capturePayment(@RequestParam String sessionId);

}
