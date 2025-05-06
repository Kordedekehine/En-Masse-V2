package com.enmasse.Product_Service.dto;

import lombok.Data;


public record ProductFilterRequestDto(
        String category,
        String brandId,
        String storeId,
        String partOfNameOrDescription
) {}
