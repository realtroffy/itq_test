package com.itq_group.orders_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itq_group.orders_service.model.DeliveryType;
import com.itq_group.orders_service.model.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;

    @NotNull(message = "Order date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;

    @NotNull(message = "Recipient cannot be null")
    @Size(min = 10, message = "Recipient name cannot be empty")
    private String recipient;

    @NotNull(message = "Address cannot be null")
    @Size(min = 10, message = "Address cannot be empty")
    private String address;

    @NotNull(message = "Payment type cannot be null")
    private PaymentType paymentType;

    @NotNull(message = "Delivery type cannot be null")
    private DeliveryType deliveryType;

    private List<OrderDetailDTO> orderDetails = new ArrayList<>();
}
