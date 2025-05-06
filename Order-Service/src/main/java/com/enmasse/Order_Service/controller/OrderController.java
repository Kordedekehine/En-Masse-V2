package com.enmasse.Order_Service.controller;

import com.enmasse.Order_Service.dtos.CapturePaymentResponse;
import com.enmasse.Order_Service.dtos.OrderRequest;
import com.enmasse.Order_Service.dtos.OrderResponse;
import com.enmasse.Order_Service.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        OrderResponse placeOrder = orderService.placeOrder(orderRequest, request);
        return ResponseEntity.ok(placeOrder);
    }

    @GetMapping("/place-order/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Long id) {
        OrderResponse getOrder = orderService.getOrder(id);
        return ResponseEntity.ok(getOrder);
    }


    @GetMapping("/getOrdersByUserId")
    public ResponseEntity<?> getOrdersByUserId(@RequestParam String id) {
        List<OrderResponse> getOrdersByUserId = orderService.getOrdersByUser(id);
        return ResponseEntity.ok(getOrdersByUserId);
    }


    @PostMapping("/confirm-order")
    public ResponseEntity<?> confirmOrderOrder(@RequestParam String sessionId) {
        CapturePaymentResponse confirmOrder = orderService.confirmPaymentAndUpdateOrder(sessionId);
        return ResponseEntity.ok(confirmOrder);
    }

}
