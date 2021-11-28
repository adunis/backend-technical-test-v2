package com.tui.proof.config;

import com.tui.proof.handler.FoodOrderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

//TODO OpenAPI

@Configuration
@RequiredArgsConstructor
public class RoutingConfig {

    private final FoodOrderHandler foodOrderHandler;

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route()
                .path("/api/v1", builder -> builder
                        .PATCH("/order", foodOrderHandler::update)
                        .POST("/order", foodOrderHandler::post)
                        .GET("/order", foodOrderHandler::get))
                .build();
    }
}
