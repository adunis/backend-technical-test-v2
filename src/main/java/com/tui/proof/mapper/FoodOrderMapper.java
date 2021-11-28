package com.tui.proof.mapper;

import com.tui.proof.dto.OrderResponseData;
import com.tui.proof.entity.client.FoodClient;
import com.tui.proof.entity.order.FoodOrder;
import org.springframework.stereotype.Component;

@Component
public class FoodOrderMapper {

    public OrderResponseData orderAndClientToOrderResponseData(FoodOrder foodOrder, FoodClient foodClient){
        return OrderResponseData.builder()
                .orderId(foodOrder.getOrderId())
                .street(foodOrder.getStreet())
                .city(foodOrder.getCity())
                .country(foodOrder.getCountry())
                .orderQuantity(foodOrder.getQuantityOrdered())
                .orderCreationDate(foodOrder.getOrderCreationDate())
                .orderTotal(foodOrder.getOrderTotal())
                .postcode(foodOrder.getPostcode())
                .clientFirstName(foodClient.getFirstName())
                .clientLastName(foodClient.getLastName())
                .clientPhoneNumber(foodClient.getPhoneNumber())
                .build();
    }



}
