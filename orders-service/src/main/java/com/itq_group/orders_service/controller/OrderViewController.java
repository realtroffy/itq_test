package com.itq_group.orders_service.controller;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/front/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;

    @GetMapping
    public String createOrderForm(Model model) {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        model.addAttribute("order", orderRequestDto);
        return "orders/create";
    }

    @PostMapping
    public String createOrder(@Valid OrderRequestDto orderRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "orders/create";
        }

        OrderDTO createdOrder = orderService.createOrder(orderRequestDto);

        model.addAttribute("order", createdOrder);
        model.addAttribute("orderDetails", createdOrder.getOrderDetails());

        return "orders/success";
    }
}
