package com.tui.proof;

import com.tui.proof.dto.OrderResponseDTO;
import com.tui.proof.dto.PatchOrderRequestDTO;
import com.tui.proof.dto.PostOrderRequestDTO;
import com.tui.proof.dto.SearchOrderRequestDTO;
import com.tui.proof.entity.client.FoodClient;
import com.tui.proof.entity.order.FoodOrder;
import com.tui.proof.repository.FoodClientRepository;
import com.tui.proof.repository.FoodOrderRepository;
import com.tui.proof.service.FoodOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class FoodOrderServiceTest{

    @Autowired FoodOrderService foodOrderService;
    @Autowired FoodOrderRepository foodOrderRepository;
    @Autowired FoodClientRepository foodClientRepository;

    @MockBean Clock clock;

    @BeforeEach void setup(){
        when(clock.instant()).thenReturn(Instant.parse("2021-12-01T10:00:00.653Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    @Test
    public void createOrder_and_Client_Test_Ok(){

        String testName = "createOrderandClientTestOk";

        Assertions.assertFalse(foodOrderRepository.findByCity(testName).isPresent());
        Assertions.assertFalse(foodClientRepository.findByFirstName(testName).isPresent());

        foodOrderService.postOrder(
                PostOrderRequestDTO.builder()
                        .clientFirstName(testName)
                        .clientLastName("testUser")
                        .city(testName)
                        .clientPhoneNumber("+32333333333333")
                        .country("testCountry")
                        .orderQuantity(10)
                        .postcode("postCode")
                        .street("testStreet")
                        .build()
        );

        Assertions.assertTrue(foodOrderRepository.findByCity(testName).isPresent());
        Assertions.assertTrue(foodClientRepository.findByFirstName(testName).isPresent());

    }

    @Test
    public void createOrder_with_existingClient_Ok(){

        FoodClient clientToSave = FoodClient.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("+3333333")
                .build();

        foodClientRepository.save(clientToSave);

        String testName = "createOrderwithexistingClientOk";

        Assertions.assertFalse(foodOrderRepository.findByCity(testName).isPresent());
        Assertions.assertTrue(foodClientRepository.findByFirstName(clientToSave.getFirstName()).isPresent());

        foodOrderService.postOrder(
                PostOrderRequestDTO.builder()
                        .clientFirstName(clientToSave.getFirstName())
                        .clientLastName(clientToSave.getLastName())
                        .city(testName)
                        .clientPhoneNumber(clientToSave.getPhoneNumber())
                        .country("testCountry")
                        .orderQuantity(10)
                        .postcode("postCode")
                        .street("testStreet")
                        .build()
        );

        Assertions.assertTrue(foodOrderRepository.findByCity(testName).isPresent());
    }

    @Test
    public void updateOrderTestOk(){

        FoodClient clientToSave = FoodClient.builder()
            .firstName("firstName")
            .lastName("lastName")
            .phoneNumber("+3333333")
            .build();

        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);

        FoodOrder foodOrderToSave = FoodOrder.builder()
                    .orderCreationDate(ZonedDateTime.now(clock))
                    .orderTotal(100.00)
                    .city("test")
                    .country("test")
                    .postcode("test")
                    .street("test")
                    .quantityOrdered(1)
                    .clientId(foodClientSaved.getClientId())
            .build();

     FoodOrder foodOrderSaved = foodOrderRepository.save(foodOrderToSave);

     when(clock.instant()).thenReturn(Instant.parse("2021-12-01T10:02:00.653Z"));

    foodOrderService.patchOrder(
                PatchOrderRequestDTO.builder()
                        .orderId(foodOrderSaved.getOrderId())
                        .city("test2")
                        .country("testCountry")
                        .orderQuantity(10)
                        .postcode("postCode")
                        .street("testStreet")
                        .build()
        );

    Assertions.assertTrue(foodOrderRepository.findByCity("test2").isPresent());
    Assertions.assertTrue(foodOrderRepository.findByCity("test2").get().getQuantityOrdered() == 10);

    }

    @Test
    public void updateOrderTest_butItsTooLate_Ko(){

        FoodClient clientToSave = FoodClient.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("+3333333")
                .build();

        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);

        FoodOrder foodOrderToSave = FoodOrder.builder()
                .orderCreationDate(ZonedDateTime.now(clock))
                .orderTotal(100.00)
                .city("test")
                .country("test")
                .postcode("test")
                .street("test")
                .quantityOrdered(1)
                .clientId(foodClientSaved.getClientId())
                .build();

        FoodOrder foodOrderSaved = foodOrderRepository.save(foodOrderToSave);

        when(clock.instant()).thenReturn(Instant.parse("2021-12-01T10:07:00.653Z"));

        try { foodOrderService.patchOrder(
                PatchOrderRequestDTO.builder()
                        .orderId(foodOrderSaved.getOrderId())
                        .city("test2")
                        .country("testCountry")
                        .orderQuantity(10)
                        .postcode("postCode")
                        .street("testStreet")
                        .build());
        } catch (ResponseStatusException e){
            Assertions.assertTrue(e.getStatus().equals(HttpStatus.BAD_REQUEST));
        }

        Assertions.assertFalse(foodOrderRepository.findByCity("test2").isPresent());
        Assertions.assertFalse(foodOrderRepository.findByCity("test").get().getQuantityOrdered() == 10);

    }

    @Test
    public void readOrderTestOk(){

        String fullTestName = "readOrderTestOk";
        String testNamePartial = "ord";

        FoodClient clientToSave = FoodClient.builder()
                .firstName(fullTestName)
                .lastName("lastName")
                .phoneNumber("+3333333")
                .build();

        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);

        FoodOrder foodOrderToSave = FoodOrder.builder()
                .orderId(1)
                .orderCreationDate(ZonedDateTime.now())
                .orderTotal(100.00)
                .city(fullTestName)
                .country("test")
                .postcode("test")
                .street("test")
                .quantityOrdered(1)
                .clientId(foodClientSaved.getClientId())
                .build();

        foodOrderRepository.save(foodOrderToSave);

        OrderResponseDTO result = foodOrderService.getOrder(
                SearchOrderRequestDTO.builder()
                        .clientSearchName(testNamePartial)
                        .build());

        Assertions.assertTrue(result.getOrderResponseData().stream().findAny().get().getClientFirstName().equals(fullTestName));
        Assertions.assertTrue(result.getOrderResponseData().stream().findAny().get().getCity().equals(fullTestName));
    }



}