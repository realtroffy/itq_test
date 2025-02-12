package com.itq_group.number_generate_service.service.impl;

import com.itq_group.number_generate_service.model.OrderNumber;
import com.itq_group.number_generate_service.repository.OrderNumberRepository;
import com.itq_group.number_generate_service.service.OrderNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderNumberServiceImpl implements OrderNumberService {

    private final OrderNumberRepository orderNumberRepository;

    public String generateOrderNumber() {
        String randomPartOrderNumber = UUID.randomUUID().toString().substring(0, 5);
        String datePartOrderNumber = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fullOrderNumber = randomPartOrderNumber + datePartOrderNumber;
        OrderNumber orderNumber = new OrderNumber();
        orderNumber.setOrderNumber(fullOrderNumber);
        orderNumberRepository.save(orderNumber);
        return fullOrderNumber;
    }
}
