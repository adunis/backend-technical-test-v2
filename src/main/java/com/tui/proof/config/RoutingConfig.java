package com.tui.proof.config;

import com.tui.proof.dto.SearchOrderRequestDTO;
import com.tui.proof.dto.OrderResponseDTO;
import com.tui.proof.dto.PatchOrderRequestDTO;
import com.tui.proof.dto.PostOrderRequestDTO;
import com.tui.proof.handler.FoodOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RoutingConfig {

    private final FoodOrderHandler foodOrderHandler;

    @RouterOperations({
            @RouterOperation( path = "/api/v1/order", method = RequestMethod.PATCH, beanClass =  FoodOrderHandler.class, beanMethod = "update",
                    operation = @Operation(operationId = "api/v1/order/", method="PATCH", summary = "Update Order",
                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = PatchOrderRequestDTO.class))),
                    responses = @ApiResponse(content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
                    description = "This method updates order details within 5 minutes of its creation")),

            @RouterOperation(path = "/api/v1/order", method = RequestMethod.POST, beanClass =  FoodOrderHandler.class, beanMethod = "post",
                    operation = @Operation(operationId = "api/v1/order/", method="PATCH", summary = "Create Order",
                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = PostOrderRequestDTO.class))),
                    responses = @ApiResponse(content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
                    description = "This method creates an order and the client if a new one")),

            @RouterOperation(path = "/api/v1/search/order", method = RequestMethod.POST, beanClass =  FoodOrderHandler.class, beanMethod = "search",
                    operation = @Operation(operationId = "/api/v1/search/order/", method="PATCH", summary = "Search Orders",
                    parameters = @Parameter(name = "Authorization", in  = ParameterIn.HEADER),
                    security = {@SecurityRequirement(name="BasicAuthentication")},
                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = SearchOrderRequestDTO.class))),
                    responses = @ApiResponse(content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
                    description = "Finds all orders which customers name contain a certain string. Requires Basic Authentication. "))
    })
    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route()
                .path("/api/v1", builder -> builder
                        .PATCH("/order", foodOrderHandler::update)
                        .POST("/order", foodOrderHandler::post)
                        .POST("/search/order", foodOrderHandler::search))
                .build();
    }
}
