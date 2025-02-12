package com.itq_group.orders_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderDetailDTO;
import com.itq_group.orders_service.dto.OrderDetailRequestDto;
import com.itq_group.orders_service.dto.OrderFilterDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.dto.OrderWithoutProductDto;
import com.itq_group.orders_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private OrderDTO orderDTO;
    private OrderFilterDTO orderFilterDTO;
    private OrderWithoutProductDto orderWithoutProductDto;
    private OrderRequestDto orderRequestDto;
    private Page<OrderDTO> ordersPage;

    @BeforeEach
    void setUp() throws ParseException {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date orderDate = dateFormat.parse("2025-02-10");
        Date startDate = dateFormat.parse("2025-01-01");
        Date endDate = dateFormat.parse("2025-02-01");

        OrderDetailDTO orderDetailDTO = OrderDetailDTO.builder()
                .id(1L)
                .itemCode(1L)
                .productName("Product 1")
                .quantity(5)
                .unitPrice(new BigDecimal("50.00"))
                .build();

        orderDTO = OrderDTO.builder()
                .id(1L)
                .orderNumber("ORD12345")
                .totalAmount(new BigDecimal("250.00"))
                .orderDate(orderDate)
                .recipient("John Doe")
                .address("123 Main Street")
                .paymentType("Credit")
                .deliveryType("Standard")
                .orderDetails(List.of(orderDetailDTO))
                .build();

        orderFilterDTO = OrderFilterDTO.builder()
                .orderDate(orderDate)
                .totalAmount(new BigDecimal("100"))
                .build();

        orderWithoutProductDto = OrderWithoutProductDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .itemCode(1L)
                .build();

        OrderDetailRequestDto orderDetailRequestDto = OrderDetailRequestDto.builder()
                .productName("Product")
                .unitPrice(new BigDecimal("250.00"))
                .quantity(1)
                .itemCode(1L)
                .build();

        orderRequestDto = OrderRequestDto.builder()
                .orderDate(orderDate)
                .address("Some address")
                .recipient("Recipient")
                .paymentType("Bank Card")
                .deliveryType("Post")
                .orderDetails(List.of(orderDetailRequestDto))
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        ordersPage = new PageImpl<>(Collections.singletonList(orderDTO), pageRequest, 1);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD12345"))
                .andExpect(jsonPath("$.totalAmount").value(250.00))
                .andExpect(jsonPath("$.orderDate").exists());
    }

    @Test
    void getById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD12345"))
                .andExpect(jsonPath("$.totalAmount").value(250.00))
                .andExpect(jsonPath("$.orderDate").exists());
    }

    @Test
    void getOrdersByFilter_ShouldReturnOrders() throws Exception {
        when(orderService.getOrdersByDateAndAmount(any(), any(), any(Pageable.class))).thenReturn(ordersPage);

        mockMvc.perform(post("/api/v1/orders/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10")
                        .content(objectMapper.writeValueAsString(orderFilterDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD12345"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(250.00));
    }

    @Test
    void getOrdersWithoutProductInDateRange_ShouldReturnOrders() throws Exception {
        when(orderService.getOrdersNotContainingProductAndInDateRange(any(), any(), any(), any(Pageable.class))).thenReturn(ordersPage);

        mockMvc.perform(post("/api/v1/orders/without-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderWithoutProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD12345"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(250.00));
    }
}