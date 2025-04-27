package com.enmasse.Product_Service.dto;

public record ProductFilterRequestDto(
        String category,
        String brandId,
        Long storeId,
        String partOfNameOrDescription
) {}
