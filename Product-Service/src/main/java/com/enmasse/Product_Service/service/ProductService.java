package com.enmasse.Product_Service.service;

import com.enmasse.Product_Service.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {

   ResponseEntity<ProductResponseDto> createProduct(ProductRequestDto productRequest, HttpServletRequest request);

   Page<ProductResponseDto> filterProducts(Pageable pageable, ProductFilterRequestDto request);

   ProductResponseDto getProduct(Long id);

   List<ProductResponseDto> getProductsByIds(List<Long> ids);

   BrandResponseDto getBrand(Long id);

   List<BrandResponseDto> getAllBrands();

   ResponseEntity<String> setProductStocks(List<ProductStockDto> stocks);

   void consumeOrderCreatedEvent(OrderCreatedEvent event);

}
