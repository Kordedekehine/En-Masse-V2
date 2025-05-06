package com.enmasse.Product_Service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Size(max = 1000000, message = "Image must be less than 1000kB")
    private byte[] image;

    @Min(value = 1, message = "Price must be at least 1")
    private BigDecimal price;

    private String category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Min(value = 0, message = "Stock must be at least 0")
    private Integer stock;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", updatable = false, nullable = false)
    private Date createdOn;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on", updatable = true, nullable = false)
    private Date updatedOn;

}
