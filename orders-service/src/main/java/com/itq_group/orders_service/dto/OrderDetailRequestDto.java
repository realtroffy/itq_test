package com.itq_group.orders_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class OrderDetailRequestDto {

    @NotNull(message = "Product code is required.")
    private Long itemCode;

    @NotNull(message = "Product name is required.")
    private String productName;

    @Positive(message = "Quantity must be greater than zero.")
    private Integer quantity;

    @NotNull(message = "Unit price is required.")
    @Positive(message = "Unit price must be greater than zero.")
    private BigDecimal unitPrice;
}
