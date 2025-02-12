package com.itq_group.orders_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
public class OrderFilterDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Order date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;

    @NotNull(message = "Total amount is required.")
    @Positive(message = "Total amount must be greater than zero.")
    private BigDecimal totalAmount;
}
