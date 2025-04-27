package com.enmasse.Product_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


public record ProductRequestDto (

   String name,
   String description,
   String image,
   BigDecimal price,
   String category,
   String brandName, //e,g Ajayi Farm, Dantata Farm
   String brandImage,
   Integer stock
){

}
