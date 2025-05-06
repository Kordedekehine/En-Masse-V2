package com.enmasse.Order_Service.client;

import com.enmasse.Order_Service.dtos.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${product-service.name}",url = "${product-service.url}")
public interface ProductClient {

    @PostMapping("/getProductIds")
    List<ProductResponseDto> getProductsByIds(@RequestParam List<Long> ids);


}
