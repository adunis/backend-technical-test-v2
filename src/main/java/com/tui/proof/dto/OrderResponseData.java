package com.tui.proof.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseData {
    private Integer orderId;
    private Integer orderQuantity;
    private Double orderTotal;
    private ZonedDateTime orderCreationDate;
    private String clientFirstName;
    private String clientLastName;
    private String clientPhoneNumber;
    private String street;
    private String postcode;
    private String city;
    private String country;
}
