package com.enmasse.Payment_Service.service;

import com.enmasse.Payment_Service.dtos.CapturePaymentResponse;
import com.enmasse.Payment_Service.dtos.CreatePaymentRequest;
import com.enmasse.Payment_Service.dtos.CreatePaymentResponse;
import com.enmasse.Payment_Service.dtos.StripeResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


public interface PaymentService {

    CreatePaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);

    CapturePaymentResponse capturePayment(String sessionId);


}
