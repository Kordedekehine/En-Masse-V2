package com.enmasse.Product_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Builder
public record ProductStockUpdateEvent(
        List<ProductStockDto> stockUpdates
) {}
