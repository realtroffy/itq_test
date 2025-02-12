package com.itq_group.number_generate_service.controller;

import com.itq_group.number_generate_service.service.OrderNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderNumberControllerTest {

    @Mock
    private OrderNumberService orderNumberService;

    @InjectMocks
    private OrderNumberController orderNumberController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderNumberController).build();
    }

    @Test
    void generateOrderNumber_ShouldReturnGeneratedNumber() throws Exception {
        String generatedNumber = "ORD123456";
        when(orderNumberService.generateOrderNumber()).thenReturn(generatedNumber);

        mockMvc.perform(get("/api/v1/numbers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(generatedNumber));
    }
}
