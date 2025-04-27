package com.enmasse.Product_Service.dto;

import lombok.Data;


public record BrandResponseDto (

  Long id,
  String name,

  String image
){
}
