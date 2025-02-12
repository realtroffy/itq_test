package com.itq_group.orders_service.swagger;

import com.itq_group.orders_service.dto.OrderDTO;
import com.itq_group.orders_service.dto.OrderFilterDTO;
import com.itq_group.orders_service.dto.OrderRequestDto;
import com.itq_group.orders_service.dto.OrderWithoutProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface OrderControllerSwagger {

    @Operation(summary = "Create a new order",
            description = "Creates a new order from the provided order details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            })
    ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequestDto orderRequestDto);

    @Operation(summary = "Get order by ID",
            description = "Find order by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    ResponseEntity<OrderDTO> getById(@PathVariable("id") long id);

    @Operation(summary = "Get orders by filter",
            description = "Fetches a paginated list of orders based on the specified filters for order date and more than total amount.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of orders",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
            })
    ResponseEntity<Page<OrderDTO>> getOrdersByFilter(@RequestBody OrderFilterDTO orderFilterDTO,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get orders without specific product in date range",
            description = "Fetches a paginated list of orders that do not contain a specific product within the provided date range.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of orders",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid input parameters")
            })
    ResponseEntity<Page<OrderDTO>> getOrdersWithoutProductInDateRange(@RequestBody OrderWithoutProductDto orderWithoutProductDto,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size);
}
