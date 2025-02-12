package com.itq_group.orders_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class OrderWithoutProductDto {

    @NotNull(message = "Product code must not be null")
    @Positive(message = "Product code must be a positive number")
    private Long itemCode;

    @NotNull(message = "Start date must not be null")
    private Date startDate;

    @NotNull(message = "End date must not be null")
    private Date endDate;
}
