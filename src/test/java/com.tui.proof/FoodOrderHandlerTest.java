package com.tui.proof;

import com.google.gson.Gson;
import com.tui.proof.dto.PatchOrderRequestDTO;
import com.tui.proof.dto.PostOrderRequestDTO;
import com.tui.proof.dto.SearchOrderRequestDTO;
import com.tui.proof.entity.client.FoodClient;
import com.tui.proof.entity.order.FoodOrder;
import com.tui.proof.handler.FoodOrderHandler;
import com.tui.proof.repository.FoodClientRepository;
import com.tui.proof.repository.FoodOrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class FoodOrderHandlerTest {



    @Autowired
    private FoodOrderHandler foodOrderHandler;
    private ServerResponse.Context context;
    @Autowired
    FoodOrderRepository foodOrderRepository;
    @Autowired
    FoodClientRepository foodClientRepository;

    @MockBean
    Clock clock;

    @BeforeEach
    void setup(){
        when(clock.instant()).thenReturn(Instant.parse("2021-12-01T10:00:00.653Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    @Test public void searchTest_ok(){

        FoodClient clientToSave = buildFoodClient("searchHandlerTest");
        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodOrder foodOrderToSave = buildFoodOrder(foodClientSaved, FoodOrder.builder().city("test"), ZonedDateTime.now(clock));
        FoodOrder foodOrderSaved = foodOrderRepository.save(foodOrderToSave);
        when(clock.instant()).thenReturn(Instant.parse("2021-12-01T10:02:00.653Z"));

        Gson gson = new Gson();

        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.post("/api/v1/search/order")
                        .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(gson.toJson(SearchOrderRequestDTO.builder()
                                .clientSearchName("test")
                                .build())));

        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        StepVerifier.create(
                        foodOrderHandler.search(serverRequest))
                .expectNextMatches(x -> {
                    Assertions.assertTrue(x.statusCode().equals(HttpStatus.OK));
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test public void postTest_ok() {

        Gson gson = new Gson();

        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(gson.toJson(PostOrderRequestDTO.builder()
                                .clientFirstName("test")
                                .clientLastName("test")
                                .city("test")
                                .clientPhoneNumber("+33333333")
                                .country("testCountry")
                                .orderQuantity(10)
                                .postcode("postCode")
                                .street("testStreet")
                                .build())));

        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        StepVerifier.create(
                        foodOrderHandler.post(serverRequest))
                .expectNextMatches(x -> {
                    Assertions.assertTrue(x.statusCode().equals(HttpStatus.OK));
                    return true;
                })
                .expectComplete()
                .verify();

    }

    @Test public void updateTest_ok(){

        FoodClient clientToSave = buildFoodClient("updateHandlerTest");
        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodOrder foodOrderToSave = buildFoodOrder(foodClientSaved, FoodOrder.builder().city("test"), ZonedDateTime.now(clock));
        FoodOrder foodOrderSaved = foodOrderRepository.save(foodOrderToSave);
        when(clock.instant()).thenReturn(Instant.parse("2021-12-01T10:02:00.653Z"));

        Gson gson = new Gson();

        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.patch("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(gson.toJson(PatchOrderRequestDTO.builder()
                                .orderId(foodOrderSaved.getOrderId())
                                .orderQuantity(10)
                                .build())));

        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        StepVerifier.create(
                        foodOrderHandler.update(serverRequest))
                .expectNextMatches(x -> {
                    Assertions.assertTrue(x.statusCode().equals(HttpStatus.OK));
                    return true;
                })
                .expectComplete()
                .verify();
    }

    private FoodOrder buildFoodOrder(FoodClient foodClientSaved, FoodOrder.FoodOrderBuilder builder, ZonedDateTime now) {
        return builder
                .orderCreationDate(now)
                .orderTotal(100.00)
                .country("test")
                .postcode("test")
                .street("test")
                .quantityOrdered(1)
                .clientId(foodClientSaved.getClientId())
                .build();
    }

    private FoodClient buildFoodClient(String fullTestName) {
        return FoodClient.builder()
                .firstName(fullTestName)
                .lastName("lastName")
                .phoneNumber("+3333333")
                .build();
    }

}
