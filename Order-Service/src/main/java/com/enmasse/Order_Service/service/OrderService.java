package com.enmasse.Order_Service.service;

import com.enmasse.Order_Service.dtos.OrderRequest;
import com.enmasse.Order_Service.dtos.OrderResponse;
import org.springframework.http.ResponseEntity;

public interface OrderService {

//Get product Details
//Update Order status using messaging queue - this is between payment and order service

    ResponseEntity<OrderResponse> placeOrder(OrderRequest orderRequest, Long userId);

    OrderResponse getOrder(Long id);
}
