package com.itq_group.orders_service.mapper;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toOrderDTO(Order order);

    Order toOrder(OrderDTO orderDTO);

    OrderDTO toOrderDTO(OrderRequestDto orderRequestDto);
}
