package com.itq_group.number_generate_service.controller;

import com.itq_group.number_generate_service.service.OrderNumberService;
import com.itq_group.number_generate_service.swagger.OrderNumberSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/numbers")
@RequiredArgsConstructor
public class OrderNumberController implements OrderNumberSwagger {

    private final OrderNumberService orderNumberService;

    @GetMapping
    public String generateOrderNumber() {
        return orderNumberService.generateOrderNumber();
    }
}
