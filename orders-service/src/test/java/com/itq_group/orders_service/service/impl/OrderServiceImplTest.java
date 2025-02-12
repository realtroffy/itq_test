package com.itq_group.orders_service.service.impl;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderDetailDTO;
import com.itq_group.orders_service.dto.OrderDetailRequestDto;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.exception.DateException;
import com.itq_group.orders_service.exception.OrderDetailsNotFoundException;
import com.itq_group.orders_service.exception.OrderNumberGeneratorServiceException;
import com.itq_group.orders_service.mapper.OrderMapper;
import com.itq_group.orders_service.model.Order;
import com.itq_group.orders_service.model.OrderDetail;
import com.itq_group.orders_service.repository.OrderDetailRepository;
import com.itq_group.orders_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private RestTemplate numberGenerateServiceRestTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDto orderRequestDto;
    private OrderDTO orderDTO;
    private Order order;
    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        OrderDetailRequestDto orderDetailRequestDto = OrderDetailRequestDto
                .builder()
                .quantity(1)
                .unitPrice(BigDecimal.ONE)
                .build();
        orderRequestDto = OrderRequestDto.builder().build();
        orderRequestDto.setOrderDetails(Collections.singletonList(orderDetailRequestDto));

        orderDTO = OrderDTO.builder().build();
        orderDTO.setOrderDetails(Collections.singletonList(OrderDetailDTO.builder().build()));

        order = new Order();
        order.setId(1L);

        orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
    }

    @Test
    void createOrder_ShouldThrowOrderDetailsNotFoundException_WhenOrderDetailsIsEmpty() {
        orderRequestDto.setOrderDetails(Collections.emptyList());

        OrderDetailsNotFoundException exception = assertThrows(OrderDetailsNotFoundException.class,
                () -> orderService.createOrder(orderRequestDto));

        assertEquals("Order details cannot be empty", exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowOrderNumberGeneratorServiceException_WhenServiceUnavailable() {
        ReflectionTestUtils.setField(orderService, "numberGenerateServiceUri", "service url");
        when(numberGenerateServiceRestTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Service unavailable"));
        when(orderMapper.toOrderDTO(any(OrderRequestDto.class))).thenReturn(orderDTO);

        OrderNumberGeneratorServiceException exception = assertThrows(OrderNumberGeneratorServiceException.class,
                () -> orderService.createOrder(orderRequestDto));

        assertEquals("Service order number generator unavailable", exception.getMessage());
    }

    @Test
    void createOrder_ShouldReturnOrderDTO_WhenOrderCreatedSuccessfully() {
        ReflectionTestUtils.setField(orderService, "numberGenerateServiceUri", "http://example.com/generate-number");
        when(numberGenerateServiceRestTemplate.getForObject(anyString(), eq(String.class))).thenReturn("12345");
        when(orderMapper.toOrderDTO(any(OrderRequestDto.class))).thenReturn(orderDTO);
        when(orderMapper.toOrder(any(OrderDTO.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderDetailRepository.saveAll(anyList())).thenReturn(Collections.singletonList(orderDetail));
        when(orderMapper.toOrderDTO(any(Order.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderRequestDto);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getOrderById_ShouldThrowNoSuchElementException_WhenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.getOrderById(1L));

        assertEquals("Order was not found by id = 1", exception.getMessage());
    }

    @Test
    void getOrderById_ShouldReturnOrderDTO_WhenOrderFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(any(Order.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    void getOrdersByDateAndAmount_ShouldReturnPageOfOrderDTO() {
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findOrdersByDateAndTotalAmount(any(Date.class), any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(orderPage);
        when(orderMapper.toOrderDTO(any(Order.class))).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getOrdersByDateAndAmount(new Date(), BigDecimal.valueOf(100), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findOrdersByDateAndTotalAmount(any(Date.class), any(BigDecimal.class), any(Pageable.class));
    }

    @Test
    void getOrdersNotContainingProductAndInDateRange_ShouldThrowDateException_WhenStartDateAfterEndDate() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() - 1000);

        DateException exception = assertThrows(DateException.class,
                () -> orderService.getOrdersNotContainingProductAndInDateRange(startDate, endDate, 1L, Pageable.unpaged()));

        assertEquals("Start date can't be after the end date", exception.getMessage());
    }

    @Test
    void getOrdersNotContainingProductAndInDateRange_ShouldReturnPageOfOrderDTO() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000);
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findOrdersNotContainingProductAndInDateRange(anyLong(), any(Date.class), any(Date.class), any(Pageable.class)))
                .thenReturn(orderPage);
        when(orderMapper.toOrderDTO(any(Order.class))).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getOrdersNotContainingProductAndInDateRange(startDate, endDate, 1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findOrdersNotContainingProductAndInDateRange(anyLong(), any(Date.class), any(Date.class), any(Pageable.class));
    }
}