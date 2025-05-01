package com.enmasse.Order_Service.client;

import com.enmasse.Order_Service.dtos.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${product-service.name}",url = "${product-service.url}")
public interface ProductClient {

    @GetMapping("/getProductIds")
    List<ProductResponseDto> getProductsByIds(List<Long> ids);


}
