package com.itq_group.number_generate_service.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "OrderNumber API", description = "Generates a unique order number")
@RequestMapping("/numbers")
public interface OrderNumberSwagger {

    @Operation(summary = "Generate Order Number",
            description = """
            Generates a unique order number in the format `XXXXXYYYYMMDD`
            - `XXXXX` - a random alphanumeric string (letters and digits, length 5)
            - `YYYYMMDD` - current date
            """)
    @GetMapping
    String generateOrderNumber();
}
