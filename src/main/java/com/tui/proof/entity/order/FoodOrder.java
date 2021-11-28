package com.tui.proof.entity.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodOrder {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private Integer orderId;
  @NotNull
  private ZonedDateTime orderCreationDate;
  @NotNull
  private Integer quantityOrdered;
  @NotNull
  private Double orderTotal;
  @NotNull
  private Integer clientId;
  @NotNull @Size(max=32,message="street name too long")
  private String street;
  @NotNull @Size(max=10,message="postcode name too long")
  private String postcode;
  @NotNull @Size(max=32,message="city name too long")
  private String city;
  @NotNull @Size(max=32,message="country name too long")
  private String country;

}
