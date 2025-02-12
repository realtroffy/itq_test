package com.itq_group.orders_service.service;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;

public interface OrderService {

    OrderDTO createOrder(OrderRequestDto orderRequestDto);

    OrderDTO getOrderById(Long id);

    Page<OrderDTO> getOrdersByDateAndAmount(Date orderDate, BigDecimal totalAmount, Pageable pageable);

    Page<OrderDTO> getOrdersNotContainingProductAndInDateRange(Date startDate, Date endDate, Long itemCode, Pageable pageable);
}
