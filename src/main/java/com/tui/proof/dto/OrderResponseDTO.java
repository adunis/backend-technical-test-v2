package com.tui.proof.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    List<OrderResponseData> orderResponseData;
}
