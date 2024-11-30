package com.order.management.model;

import com.order.management.enumuration.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class Product implements Serializable {

    private Long id;

    private String productName;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal price;

    private Integer availableQuantity;

    private Boolean isAvailable;

}
