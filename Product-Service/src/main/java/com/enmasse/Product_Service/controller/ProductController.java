package com.enmasse.Product_Service.controller;

import com.enmasse.Product_Service.dto.*;
import com.enmasse.Product_Service.repository.BrandRepository;
import com.enmasse.Product_Service.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDto productRequest, HttpServletRequest request) {
        String response = String.valueOf(productService.createProduct(productRequest, request));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/goods/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping("/getProductIds")
    List<ProductResponseDto> getProductsByIds(@RequestParam List<Long> ids){

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

    @GetMapping("/brands")
    public List<BrandResponseDto> getAllBrands() {
        return productService.getAllBrands();
    }

    @PostMapping("/stocks")
    public ResponseEntity<String> setProductStocks(@RequestBody List<ProductStockDto> stocks) {
        return productService.setProductStocks(stocks);
    }

}
