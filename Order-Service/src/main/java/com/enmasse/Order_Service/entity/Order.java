package com.enmasse.Order_Service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.Instant;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String sessionId;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @PastOrPresent(message = "Created date must be in the past or present")
    private Instant created;
}
