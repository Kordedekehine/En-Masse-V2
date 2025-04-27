package com.enmasse.Payment_Service.service;

import com.enmasse.Payment_Service.dtos.CreatePaymentRequest;
import com.enmasse.Payment_Service.dtos.StripeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


public interface PaymentService {

    StripeResponse createPayment(CreatePaymentRequest createPaymentRequest);

    StripeResponse capturePayment(String sessionId);


}
