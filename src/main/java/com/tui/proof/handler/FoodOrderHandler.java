package com.tui.proof.handler;

import com.tui.proof.dto.CreateOrderRequestDTO;
import com.tui.proof.dto.ReadOrderRequestDTO;
import com.tui.proof.dto.UpdateOrderRequestDTO;
import com.tui.proof.service.FoodOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class FoodOrderHandler {

    private final FoodOrderService foodOrderService;

    public Mono<ServerResponse> get (ServerRequest request) {

           return request
                .bodyToMono(ReadOrderRequestDTO.class)
                .map(dto -> foodOrderService.readOrder(dto))
                .flatMap(data -> ServerResponse.ok().body(BodyInserters.fromValue(data)))
                .doOnError(e -> log.error(e.toString()))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> post (ServerRequest request){
        return request
                .bodyToMono(CreateOrderRequestDTO.class)
                .map(dto -> foodOrderService.createOrder(dto))
                .flatMap(data -> ServerResponse.ok().body(BodyInserters.fromValue(data)))
                .doOnError(e -> log.error(e.toString()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update (ServerRequest request){
        return request
                .bodyToMono(UpdateOrderRequestDTO.class)
                .map(dto -> foodOrderService.updateOrder(dto))
                .flatMap(data -> ServerResponse.ok().body(BodyInserters.fromValue(data)))
                .doOnError(e -> log.error(e.toString()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
