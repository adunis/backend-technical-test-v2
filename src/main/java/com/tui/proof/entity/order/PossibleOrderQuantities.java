package com.tui.proof.entity.order;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public enum PossibleOrderQuantities {
   FiveOrdered(5), TenOrdered(10), FifteenOrdered(15);

   @Getter
   @Setter
   @JsonValue
   private Integer value;
}

