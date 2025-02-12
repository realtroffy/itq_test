package com.itq_group.orders_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${number.generate.service.url}")
    private String numberGenerateServiceUrl;

    @Bean
    public RestTemplate numberGenerateServiceRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(numberGenerateServiceUrl)
                .build();
    }
}
