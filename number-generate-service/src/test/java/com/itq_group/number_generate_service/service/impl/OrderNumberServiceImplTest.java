package com.itq_group.number_generate_service.service.impl;

import com.itq_group.number_generate_service.model.OrderNumber;
import com.itq_group.number_generate_service.repository.OrderNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderNumberServiceImplTest {

    @Mock
    private OrderNumberRepository orderNumberRepository;

    @InjectMocks
    private OrderNumberServiceImpl orderNumberService;

    private static final int RANDOM_PART_LENGTH = 5;
    private static final int TOTAL_ORDER_NUMBER_LENGTH = 13;
    private String expectedDatePart;

    @BeforeEach
    void setUp() {
        expectedDatePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    @Test
    void generateOrderNumber_ShouldGenerateAndSaveOrderNumber() {

        String generatedOrderNumber = orderNumberService.generateOrderNumber();

        assertEquals(TOTAL_ORDER_NUMBER_LENGTH, generatedOrderNumber.length());
        assertEquals(expectedDatePart, generatedOrderNumber.substring(RANDOM_PART_LENGTH));
        verify(orderNumberRepository, times(1)).save(any(OrderNumber.class));
    }
}