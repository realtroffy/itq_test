package com.itq_group.orders_service.service.impl;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.exception.DateException;
import com.itq_group.orders_service.exception.OrderDetailsNotFoundException;
import com.itq_group.orders_service.exception.OrderNumberGeneratorServiceException;
import com.itq_group.orders_service.mapper.OrderMapper;
import com.itq_group.orders_service.model.Order;
import com.itq_group.orders_service.model.OrderDetail;
import com.itq_group.orders_service.repository.OrderDetailRepository;
import com.itq_group.orders_service.repository.OrderRepository;
import com.itq_group.orders_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String NO_SUCH_ORDER_EXCEPTION_MESSAGE = "Order was not found by id = ";
    private static final String EMPTY_ORDER_EXCEPTION_MESSAGE = "Order details cannot be empty";
    private static final String DATE_EXCEPTION_MESSAGE = "Start date can't be after the end date";
    private static final String SERVICE_UNAVAILABLE_EXCEPTION_MESSAGE = "Service order number generator unavailable";

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RestTemplate numberGenerateServiceRestTemplate;

    @Value("${number.generate.service.numbers-uri}")
    private String numberGenerateServiceUri;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderRequestDto orderRequestDto) {
        if (orderRequestDto.getOrderDetails() == null || orderRequestDto.getOrderDetails().isEmpty()) {
            throw new OrderDetailsNotFoundException(EMPTY_ORDER_EXCEPTION_MESSAGE);
        }

        BigDecimal totalAmount = calculateTotalAmount(orderRequestDto);

        OrderDTO orderDTO = orderMapper.toOrderDTO(orderRequestDto);

        orderDTO.setTotalAmount(totalAmount);
        orderDTO.setOrderDate(new Date());
        Order order = orderMapper.toOrder(orderDTO);

        String orderNumber;

        orderNumber = getOrderNumberFromGeneratorService();

        order.setOrderNumber(orderNumber);
        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetails = orderDTO.getOrderDetails().stream()
                .map(detailDTO -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(savedOrder);
                    orderDetail.setItemCode(detailDTO.getItemCode());
                    orderDetail.setProductName(detailDTO.getProductName());
                    orderDetail.setQuantity(detailDTO.getQuantity());
                    orderDetail.setUnitPrice(detailDTO.getUnitPrice());
                    return orderDetail;
                })
                .toList();

        orderDetailRepository.saveAll(orderDetails);
        return orderMapper.toOrderDTO(savedOrder);
    }

    private String getOrderNumberFromGeneratorService() {
        String orderNumber;
        try {
             orderNumber = numberGenerateServiceRestTemplate.getForObject(numberGenerateServiceUri, String.class);
        }  catch (RestClientException e) {
            throw new OrderNumberGeneratorServiceException(SERVICE_UNAVAILABLE_EXCEPTION_MESSAGE);
        }
        return orderNumber;
    }

    private  BigDecimal calculateTotalAmount(OrderRequestDto orderRequestDto) {
        return orderRequestDto.getOrderDetails().stream()
                .map(detailDTO -> detailDTO.getUnitPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(NO_SUCH_ORDER_EXCEPTION_MESSAGE + id));
        return orderMapper.toOrderDTO(order);
    }

    @Override
    public Page<OrderDTO> getOrdersByDateAndAmount(Date orderDate, BigDecimal totalAmount, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findOrdersByDateAndTotalAmount(orderDate, totalAmount, pageable);
        return ordersPage.map(orderMapper::toOrderDTO);
    }

    @Override
    public Page<OrderDTO> getOrdersNotContainingProductAndInDateRange(Date startDate, Date endDate, Long itemCode, Pageable pageable) {
        checkFromIsBeforeTo(startDate, endDate);
        Page<Order> ordersPage = orderRepository.findOrdersNotContainingProductAndInDateRange(itemCode, startDate, endDate, pageable);
        return ordersPage.map(orderMapper::toOrderDTO);
    }

    private void checkFromIsBeforeTo(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new DateException(DATE_EXCEPTION_MESSAGE);
        }
    }
}
