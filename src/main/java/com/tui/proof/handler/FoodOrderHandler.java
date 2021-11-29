package com.tui.proof.handler;

import com.tui.proof.dto.PostOrderRequestDTO;
import com.tui.proof.dto.GetOrderRequestDTO;
import com.tui.proof.dto.PatchOrderRequestDTO;
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
                .bodyToMono(GetOrderRequestDTO.class)
                .map(dto -> foodOrderService.getOrder(dto))
                .flatMap(data -> ServerResponse.ok().body(BodyInserters.fromValue(data)))
                .doOnError(e -> log.error(e.toString()))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> post (ServerRequest request){
        return request
                .bodyToMono(PostOrderRequestDTO.class)
                .map(dto -> foodOrderService.postOrder(dto))
                .flatMap(data -> ServerResponse.ok().body(BodyInserters.fromValue(data)))
                .doOnError(e -> log.error(e.toString()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update (ServerRequest request){
        return request
                .bodyToMono(PatchOrderRequestDTO.class)
                .map(dto -> foodOrderService.patchOrder(dto))
                .flatMap(data -> ServerResponse.ok().body(BodyInserters.fromValue(data)))
                .doOnError(e -> log.error(e.toString()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
