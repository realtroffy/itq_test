package com.itq_group.orders_service.controller;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderFilterDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.dto.OrderWithoutProductDto;
import com.itq_group.orders_service.service.OrderService;
import com.itq_group.orders_service.swagger.OrderControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerSwagger {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.status(CREATED).body(orderService.createOrder(orderRequestDto));

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<OrderDTO>> getOrdersByFilter(
            @Valid @RequestBody OrderFilterDTO orderFilterDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getOrdersByDateAndAmount(orderFilterDTO.getOrderDate(),
                orderFilterDTO.getTotalAmount(), pageable));
    }

    @PostMapping("/without-product")
    public ResponseEntity<Page<OrderDTO>> getOrdersWithoutProductInDateRange(
            @RequestBody @Valid OrderWithoutProductDto orderWithoutProductDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrdersNotContainingProductAndInDateRange(
                orderWithoutProductDto.getStartDate(),
                orderWithoutProductDto.getEndDate(),
                orderWithoutProductDto.getItemCode(),
                pageable
        );
        return ResponseEntity.ok(orders);
    }
}
