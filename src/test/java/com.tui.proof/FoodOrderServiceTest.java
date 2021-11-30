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
    public void createOrder_and_Client_Test_Ko_IncorrectOrderQuantity_Ko(){

        String testName = "incorrectOrderQuantity";
        Assertions.assertFalse(foodOrderRepository.findByCity(testName).isPresent());
        Assertions.assertFalse(foodClientRepository.findByFirstName(testName).isPresent());

       try {
           foodOrderService.postOrder(
                   PostOrderRequestDTO.builder()
                           .clientFirstName(testName)
                           .clientLastName("testUser")
                           .city(testName)
                           .clientPhoneNumber("+32333333333333")
                           .country("testCountry")
                           .orderQuantity(20)
                           .postcode("postCode")
                           .street("testStreet")
                           .build()
           );
       } catch (ResponseStatusException e){
        Assertions.assertTrue(e.getStatus().equals(HttpStatus.BAD_REQUEST));
    }

        Assertions.assertFalse(foodOrderRepository.findByCity(testName).isPresent());
        Assertions.assertFalse(foodClientRepository.findByFirstName(testName).isPresent());

    }

    @Test
    public void createOrder_with_existingClient_Ok(){

        FoodClient clientToSave = buildFoodClient("firstName");
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

        FoodClient clientToSave = buildFoodClient("firstName");
        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodOrder foodOrderToSave = buildFoodOrder(foodClientSaved, FoodOrder.builder().city("testCity"), ZonedDateTime.now(clock));
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

        FoodClient clientToSave = buildFoodClient("firstName");
        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodOrder foodOrderToSave = buildFoodOrder(foodClientSaved, FoodOrder.builder().city("updateTestTooLate"), ZonedDateTime.now(clock));
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
        Assertions.assertFalse(foodOrderRepository.findByCity("updateTestTooLate").get().getQuantityOrdered() == 10);

    }


    @Test
    public void updateOrderTest_NotFound_Ko(){

        try { foodOrderService.patchOrder(
                PatchOrderRequestDTO.builder()
                        .orderId(999)
                        .city("test2")
                        .country("testCountry")
                        .orderQuantity(10)
                        .postcode("postCode")
                        .street("testStreet")
                        .build());
        } catch (ResponseStatusException e){
            Assertions.assertTrue(e.getStatus().equals(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void readOrderTest_1result_Ok(){

        String fullTestName = "Lourde";
        String testNamePartial = "our";

        FoodClient clientToSave = buildFoodClient(fullTestName);
        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodOrder foodOrderToSave = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName), ZonedDateTime.now());

        foodOrderRepository.save(foodOrderToSave);
        OrderResponseDTO result = foodOrderService.getOrder(
                SearchOrderRequestDTO.builder()
                        .clientSearchName(testNamePartial)
                        .build());

        Assertions.assertTrue(result.getOrderResponseData().stream().findAny().get().getClientFirstName().equals(fullTestName));
        Assertions.assertTrue(result.getOrderResponseData().stream().findAny().get().getCity().equals(fullTestName));
        Assertions.assertTrue(result.getOrderResponseData().stream().count() == 1);

    }

    @Test
    public void readOrderTest_MultipleResults_4results_Ok(){

        String fullTestName = "Romeo";
        String testNamePartial = "omeo";
        FoodClient clientToSave = buildFoodClient(fullTestName);
        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);

        FoodOrder foodOrderToSave1 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"1"), ZonedDateTime.now());
        FoodOrder foodOrderToSave2 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"2"), ZonedDateTime.now());
        FoodOrder foodOrderToSave3 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"3"), ZonedDateTime.now());
        FoodOrder foodOrderToSave4 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"4"), ZonedDateTime.now());

        foodOrderRepository.save(foodOrderToSave1);
        foodOrderRepository.save(foodOrderToSave2);
        foodOrderRepository.save(foodOrderToSave3);
        foodOrderRepository.save(foodOrderToSave4);

        OrderResponseDTO result = foodOrderService.getOrder(
                SearchOrderRequestDTO.builder()
                        .clientSearchName(testNamePartial)
                        .build());

        Assertions.assertTrue(result.getOrderResponseData().stream().count() == 4);
    }

    @Test
    public void readOrderTest_MultipleResults_2_Clients_8results_Ok(){

        String fullTestName = "Romolo";
        String fullTestName2 = "Remo";
        String testNamePartial = "mo";
        FoodClient clientToSave = buildFoodClient(fullTestName);
        FoodClient clientToSave2 = buildFoodClient(fullTestName2);

        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodClient foodClientSaved2 = foodClientRepository.save(clientToSave2);

        FoodOrder foodOrderToSave1 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"1"), ZonedDateTime.now());
        FoodOrder foodOrderToSave2 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"2"), ZonedDateTime.now());
        FoodOrder foodOrderToSave3 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"3"), ZonedDateTime.now());
        FoodOrder foodOrderToSave4 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"4"), ZonedDateTime.now());

        FoodOrder foodOrderToSave5 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"5"), ZonedDateTime.now());
        FoodOrder foodOrderToSave6 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"6"), ZonedDateTime.now());
        FoodOrder foodOrderToSave7 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"7"), ZonedDateTime.now());
        FoodOrder foodOrderToSave8 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"8"), ZonedDateTime.now());

        foodOrderRepository.save(foodOrderToSave1);
        foodOrderRepository.save(foodOrderToSave2);
        foodOrderRepository.save(foodOrderToSave3);
        foodOrderRepository.save(foodOrderToSave4);
        foodOrderRepository.save(foodOrderToSave5);
        foodOrderRepository.save(foodOrderToSave6);
        foodOrderRepository.save(foodOrderToSave7);
        foodOrderRepository.save(foodOrderToSave8);

        OrderResponseDTO result = foodOrderService.getOrder(
                SearchOrderRequestDTO.builder()
                        .clientSearchName(testNamePartial)
                        .build());

        Assertions.assertTrue(result.getOrderResponseData().stream().count() == 8);
    }

    @Test
    public void readOrderTest_MultipleResults_2_Clients_4results_Ok(){

        String fullTestName = "readOrderTestOk";
        String fullTestName2 = "readerTestOk2";
        String testNamePartial = "ord";

        FoodClient clientToSave = buildFoodClient(fullTestName);

        FoodClient clientToSave2 = buildFoodClient(fullTestName2);

        FoodClient foodClientSaved = foodClientRepository.save(clientToSave);
        FoodClient foodClientSaved2 = foodClientRepository.save(clientToSave2);

        FoodOrder foodOrderToSave1 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"1"), ZonedDateTime.now());
        FoodOrder foodOrderToSave2 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"2"), ZonedDateTime.now());
        FoodOrder foodOrderToSave3 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"3"), ZonedDateTime.now());
        FoodOrder foodOrderToSave4 = buildFoodOrder(foodClientSaved, FoodOrder.builder().city(fullTestName+"4"), ZonedDateTime.now());

        FoodOrder foodOrderToSave5 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"5"), ZonedDateTime.now());
        FoodOrder foodOrderToSave6 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"6"), ZonedDateTime.now());
        FoodOrder foodOrderToSave7 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"7"), ZonedDateTime.now());
        FoodOrder foodOrderToSave8 = buildFoodOrder(foodClientSaved2, FoodOrder.builder().city(fullTestName2+"8"), ZonedDateTime.now());

        foodOrderRepository.save(foodOrderToSave1);
        foodOrderRepository.save(foodOrderToSave2);
        foodOrderRepository.save(foodOrderToSave3);
        foodOrderRepository.save(foodOrderToSave4);
        foodOrderRepository.save(foodOrderToSave5);
        foodOrderRepository.save(foodOrderToSave6);
        foodOrderRepository.save(foodOrderToSave7);
        foodOrderRepository.save(foodOrderToSave8);

        OrderResponseDTO result = foodOrderService.getOrder(
                SearchOrderRequestDTO.builder()
                        .clientSearchName(testNamePartial)
                        .build());

        Assertions.assertTrue(result.getOrderResponseData().stream().count() == 4);
    }

    @Test
    public void readOrderTest_NoResults_Ko(){

   try {    OrderResponseDTO result = foodOrderService.getOrder(
                SearchOrderRequestDTO.builder()
                        .clientSearchName("a")
                        .build());
    } catch (ResponseStatusException e) {
       Assertions.assertTrue(e.getStatus().equals(HttpStatus.NOT_FOUND));
   }

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