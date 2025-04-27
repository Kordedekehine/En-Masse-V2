package com.enmasse.Order_Service.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductStockUpdateEvent(
        List<ProductStockDto> stockUpdates
) {}
