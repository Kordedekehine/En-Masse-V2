package com.enmasse.Product_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
public record ProductResponseDto(

        Long id,
        String storeId,
        String name,
        String description,
        String image,
        BigDecimal price,
        String category,
        Long brandId,
        String brandName,
        Integer stock,
        String createdOn,
        String updatedOn)
 {



//    private String name;
//    private Long storeId;
//    private String description;
//    private String image;
//    private BigDecimal price;
//    private String category;
//    private String brandId;
//    private String brandName;
//    private Integer stock;
//    private String created_on;
//    private String updated_on;

}
