package com.enmasse.Product_Service.service;

import com.enmasse.Product_Service.dto.*;
import com.enmasse.Product_Service.entity.Brand;
import com.enmasse.Product_Service.entity.Product;
import com.enmasse.Product_Service.repository.BrandRepository;
import com.enmasse.Product_Service.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;


    private ProductResponseDto mapProductToResponse(Product product) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String createdOnFormatted = product.getCreatedOn() != null ? sdf.format(product.getCreatedOn()) : null;
        String updatedOnFormatted = product.getUpdatedOn() != null ? sdf.format(product.getUpdatedOn()) : null;

        return new ProductResponseDto(
                product.getId(),
                product.getStoreId(),
                product.getName(),
                product.getDescription(),
                new String(product.getImage() != null ? product.getImage() : new byte[0]),
                product.getPrice(),
                product.getCategory(),
                product.getBrand() == null ? null : product.getBrand().getId(),
                product.getBrand() == null ? null : product.getBrand().getName(),
                product.getStock(),
                createdOnFormatted,
                updatedOnFormatted
        );
    }


    public ResponseEntity<ProductResponseDto> createProduct(ProductRequestDto productRequest, Long userId) {
        // create brand if not found
        Brand brand = brandRepository.findByName(productRequest.brandName())
                .orElseGet(() -> {
                    Brand newBrand = Brand.builder()
                            .name(productRequest.brandName())
                            .image(productRequest.brandImage().getBytes())
                            .build();

                    brandRepository.save(newBrand);

                    return newBrand;
                });

        Product product = Product.builder()
                .storeId(userId)
                .name(productRequest.name())
                .description(productRequest.description())
                .image(productRequest.image().getBytes())
                .price(productRequest.price())
                .category(productRequest.category())
                .brand(brandRepository.findById(brand.getId())
                        .orElseThrow(() -> new RuntimeException("Brand not found")))
                .stock(productRequest.stock())
                .createdOn(Date.from(Instant.now()))
                .build();

        productRepository.save(product);

        return new ResponseEntity<>(mapProductToResponse(product), HttpStatus.CREATED);
    }


    private BrandResponseDto mapBrandToResponse(Brand brand) {
        return new BrandResponseDto(
                brand.getId(),
                brand.getName(),
                new String(brand.getImage() != null ? brand.getImage() : new byte[0])
        );
    }

    public Page<ProductResponseDto> filterProducts(Pageable pageable, ProductFilterRequestDto request) {
        Page<Product> page = productRepository.findByFilters(pageable, request);
        return page.map(this::mapProductToResponse);
    }


    public ProductResponseDto getProduct(Long id) {
        return productRepository.findById(id)
                .map(this::mapProductToResponse)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<ProductResponseDto> getProductsByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);

        List<Long> foundIds = products.stream()
                .map(Product::getId)
                .toList();

        List<Long> missingIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new RuntimeException("The following product IDs were not found: " + missingIds);
        }

        return products.stream()
                .map(this::mapProductToResponse)
                .toList();
    }


    public BrandResponseDto getBrand(Long id) {
        return brandRepository.findById(id)
                .map(this::mapBrandToResponse)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    public List<BrandResponseDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(this::mapBrandToResponse)
                .toList();
    }


    public ResponseEntity<String> setProductStocks(List<ProductStockDto> stocks) {
        List<Product> products = new ArrayList<>();
        for (ProductStockDto stock : stocks) {
            if (stock.quantity() < 0)
                return new ResponseEntity<>("Stock quantity cannot be negative", HttpStatus.BAD_REQUEST);

            Optional<Product> productOptional = productRepository.findById(stock.id());
            if (productOptional.isEmpty())
                return new ResponseEntity<>("Product " + stock.id() + " not found", HttpStatus.NOT_FOUND);

            Product product = productOptional.get();
            product.setStock(stock.quantity());
            products.add(product);
        }

        productRepository.saveAll(products);

        return new ResponseEntity<>("Stocks updated", HttpStatus.OK);
    }

    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        List<OrderItemRequest> orderedItems = event.items();
        List<Product> updatedProducts = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderedItems) {
            Optional<Product> productOptional = productRepository.findById(itemRequest.productId());

            if (productOptional.isEmpty()) {
                log.warn("Product not found for ID: {}", itemRequest.productId());
                continue;
            }

            Product product = productOptional.get();

            if (product.getStock() < itemRequest.quantity()) {
                log.error("Insufficient stock for Product ID: {}. Available: {}, Needed: {}",
                        product.getId(), product.getStock(), itemRequest.quantity());
                continue;
            }

            // Update stock
            product.setStock(product.getStock() - itemRequest.quantity());
            updatedProducts.add(product);

            log.info("Prepared stock update for product: {}, New stock: {}",
                    product.getName(), product.getStock());
        }

        if (!updatedProducts.isEmpty()) {
            productRepository.saveAll(updatedProducts);
            log.info("Successfully updated stock for {} products.", updatedProducts.size());
        } else {
            log.warn("No product stocks were updated.");
        }
    }
}
