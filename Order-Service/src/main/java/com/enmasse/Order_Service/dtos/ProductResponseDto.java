package com.enmasse.Order_Service.dtos;

import java.math.BigDecimal;

public record ProductResponseDto(

        Long id,
        Long storeId,
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
