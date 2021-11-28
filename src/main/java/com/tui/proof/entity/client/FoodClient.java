package com.tui.proof.entity.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodClient {
  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private Integer clientId;
  @NotNull @Size(max=32,message="first name name too long")
  private String firstName;
  @NotNull  @Size(max=32,message="last name name too long")
  private String lastName;
  @NotNull @Pattern(regexp="^\\+[1-9]{1}[0-9]{3,14}$",message="phone number format not correct")
  private String phoneNumber;

}
