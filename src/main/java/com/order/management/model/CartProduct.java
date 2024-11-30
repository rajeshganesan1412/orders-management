package com.order.management.model;

import com.order.management.enumuration.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct implements Serializable {

    private Long cartProductId;

    private String productName;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal price;

    private Integer quantity;
}
