package com.enmasse.Product_Service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
public record ProductStockDto(

     Long id,
     int quantity
) {
}
