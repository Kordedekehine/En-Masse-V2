package com.enmasse.Order_Service.dtos;

import java.util.List;
import java.util.Map;

public record OrderRequest(
    List<OrderItemRequest> items,
    String deliveryAddress
){}
