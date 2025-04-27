package com.enmasse.Order_Service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;


@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Product id is required")
    private Long productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @Min(value = 1, message = "Price must be at least 1")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
