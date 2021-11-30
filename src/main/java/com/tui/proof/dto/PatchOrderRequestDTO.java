package com.tui.proof.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchOrderRequestDTO {
    private Integer orderId;
    private Integer orderQuantity;
    @Size(max=32,message="street name too long")
    private String street;
    @Size(max=32,message="postcode name too long")
    private String postcode;
    @Size(max=32,message="city name too long")
    private String city;
    @Size(max=32,message="country name too long")
    private String country;
}
