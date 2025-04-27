package com.enmasse.Product_Service.controller;

import com.enmasse.Product_Service.dto.*;
import com.enmasse.Product_Service.repository.BrandRepository;
import com.enmasse.Product_Service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequest, @RequestParam(name="userId") Long userId) {
        return productService.createProduct(productRequest, userId);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/getProductIds")
    List<ProductDetailsResponse> getProductsByIds(List<Long> ids){
        return productService.getProductsByIds(ids);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<ProductResponseDto>> filterProducts(
            @RequestBody ProductFilterRequestDto request,
            @PageableDefault(size = 10, sort = "createdOn", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.filterProducts(pageable, request));
    }


    @GetMapping("/{id}/image")
    public String getProductImage(@PathVariable Long id) {
        return productService.getProduct(id).image();
    }

    @GetMapping("/brand/{id}")
    public BrandResponseDto getBrand(@PathVariable Long id) {
        return productService.getBrand(id);
    }

    @GetMapping("/brand")
    public List<BrandResponseDto> getAllBrands() {
        return productService.getAllBrands();
    }

    @PostMapping("/stocks")
    public ResponseEntity<String> setProductStocks(@RequestBody List<ProductStockDto> stocks) {
        return productService.setProductStocks(stocks);
    }

}
