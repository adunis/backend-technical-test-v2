package com.tui.proof.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {
    @NotNull
    private Integer orderQuantity;
    @NotBlank @Size(max=32,message="first name name too long")
    private String clientFirstName;
    @NotBlank @Size(max=32,message="last name name too long")
    private String clientLastName;
    @NotBlank @Pattern(regexp="^\\+[1-9]{1}[0-9]{3,14}$",message="phone number format not correct")
    private String clientPhoneNumber;
    @NotBlank @Size(max=32,message="stree name too long")
    private String street;
    @NotBlank @Size(max=32,message="postcode name too long")
    private String postcode;
    @NotBlank @Size(max=32,message="city name too long")
    private String city;
    @NotBlank @Size(max=32,message="country name too long")
    private String country;

}
