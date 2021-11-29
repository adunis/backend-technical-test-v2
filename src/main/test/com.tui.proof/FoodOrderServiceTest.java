package com.tui.proof;

import com.tui.proof.dto.PostOrderRequestDTO;
import com.tui.proof.repository.FoodClientRepository;
import com.tui.proof.repository.FoodOrderRepository;
import com.tui.proof.service.FoodOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FoodOrderServiceTest{

    //TODO

    @Autowired private  FoodOrderService foodOrderService;
    @Autowired private  FoodOrderRepository foodOrderRepository;
    @Autowired private  FoodClientRepository foodClientRepository;

    @Test
    public void createOrderTest(){
        assertFalse(foodOrderRepository.findById(2).isPresent());
        foodOrderService.postOrder(
                PostOrderRequestDTO.builder()
                        .clientFirstName("testUser")
                        .clientLastName("testUser")
                        .city("testCity")
                        .clientPhoneNumber("+32333333333333")
                        .country("testCountry")
                        .orderQuantity(10)
                        .postcode("postCode")
                        .street("testStreet")
                        .build()
        );
        assertTrue(foodOrderRepository.findById(2).isPresent());
    }

    @Test
    public void updateOrderTest(){

    }

    @Test
    public void readOrderTest(){

    }



}