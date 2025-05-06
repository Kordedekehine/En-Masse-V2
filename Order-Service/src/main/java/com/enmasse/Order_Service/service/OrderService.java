package com.enmasse.Order_Service.service;

import com.enmasse.Order_Service.dtos.CapturePaymentResponse;
import com.enmasse.Order_Service.dtos.OrderRequest;
import com.enmasse.Order_Service.dtos.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest, HttpServletRequest request);

    OrderResponse getOrder(Long id);

    CapturePaymentResponse confirmPaymentAndUpdateOrder(String sessionId);

    List<OrderResponse> getOrdersByUser(String userId);

}
