package com.tui.proof.service;

import com.tui.proof.dto.PostOrderRequestDTO;
import com.tui.proof.dto.OrderResponseDTO;
import com.tui.proof.dto.GetOrderRequestDTO;
import com.tui.proof.dto.PatchOrderRequestDTO;
import com.tui.proof.entity.client.FoodClient;
import com.tui.proof.entity.order.FoodOrder;
import com.tui.proof.mapper.FoodOrderMapper;
import com.tui.proof.repository.FoodClientRepository;
import com.tui.proof.repository.FoodOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class FoodOrderService {

    @Value("${item-price}")
    private Double itemPrice;
    @Value("${minute-time-limit-for-updating-order}")
    private Integer minuteTimeLimitForUpdatingOrder;
    @Value("${accepted-order-quantities}")
    private List<Integer> acceptedOrderQuantities;

    private final FoodOrderRepository foodOrderRepository;
    private final FoodClientRepository foodClientRepository;
    private final FoodOrderMapper clientMapper;


    public OrderResponseDTO postOrder(@Valid PostOrderRequestDTO dto) {

        if (!acceptedOrderQuantities.contains(dto.getOrderQuantity()) ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order value not accepted, only: " + acceptedOrderQuantities.toString());

        FoodClient foodClientThatMadeTheOrder = getClientThatMadeTheOrder(dto);

        FoodOrder foodOrderToSave = FoodOrder.builder()
                .orderCreationDate(ZonedDateTime.now())
                .orderTotal(round(itemPrice * dto.getOrderQuantity(), 2))
                .quantityOrdered(dto.getOrderQuantity())
                .clientId(foodClientThatMadeTheOrder.getClientId())
                .city(dto.getCity())
                .country(dto.getCountry())
                .street(dto.getStreet())
                .postcode(dto.getPostcode())
                .build();

        FoodOrder savedFoodOrder = foodOrderRepository.save(foodOrderToSave);

        return OrderResponseDTO.builder()
                .orderResponseData(List.of(
                        clientMapper.orderAndClientToOrderResponseData(savedFoodOrder, foodClientThatMadeTheOrder))
                )
                .build();
    }

    private  Double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }


    private FoodClient getClientThatMadeTheOrder(PostOrderRequestDTO dto) {
        FoodClient foodClientThatMadeTheOrder = null;
        Optional<FoodClient> clientOpt = foodClientRepository.findByFirstNameAndLastNameAndPhoneNumber(dto.getClientFirstName(), dto.getClientLastName(), dto.getClientPhoneNumber());
        if (clientOpt.isEmpty()){
            FoodClient foodClientToSave = FoodClient.builder()
                    .firstName(dto.getClientFirstName())
                    .lastName(dto.getClientLastName())
                    .phoneNumber(dto.getClientPhoneNumber())
                    .build();

            foodClientThatMadeTheOrder = foodClientRepository.save(foodClientToSave);
        }

        if (clientOpt.isPresent()){
            foodClientThatMadeTheOrder = clientOpt.get();
        }

        return foodClientThatMadeTheOrder;

    }

    public OrderResponseDTO patchOrder(PatchOrderRequestDTO dto)  {

       Optional<FoodOrder> orderToUpdateOpt = foodOrderRepository.findById(dto.getOrderId());
       if (orderToUpdateOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order does not exist");

       FoodOrder foodOrderToUpdate = orderToUpdateOpt.get();
       ZonedDateTime currentDateTime = ZonedDateTime.now();
       Duration differenceNowAndOrderCreation = Duration.between(foodOrderToUpdate.getOrderCreationDate(), currentDateTime);

       if (differenceNowAndOrderCreation.toMinutes() > minuteTimeLimitForUpdatingOrder) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't update the order anymore after 5 minutes since its creation");

       if (dto.getOrderQuantity() != null) {
           foodOrderToUpdate.setQuantityOrdered(dto.getOrderQuantity());
           foodOrderToUpdate.setOrderTotal(round(itemPrice * dto.getOrderQuantity(), 2));
       }
       if (dto.getCity() != null) foodOrderToUpdate.setCity(dto.getCity());
       if (dto.getCountry() != null) foodOrderToUpdate.setCountry(dto.getCountry());
       if (dto.getPostcode() != null) foodOrderToUpdate.setPostcode(dto.getPostcode());
       if (dto.getStreet() != null) foodOrderToUpdate.setStreet(dto.getStreet());

       FoodOrder savedFoodOrder = foodOrderRepository.save(foodOrderToUpdate);
       Optional<FoodClient> ordersClient = foodClientRepository.findById(savedFoodOrder.getClientId());

        if (ordersClient.isEmpty()) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "This order has no client - internal db is inconsistent");

        return OrderResponseDTO.builder()
                .orderResponseData(List.of(
                        clientMapper.orderAndClientToOrderResponseData(savedFoodOrder, ordersClient.get())))
                .build();
    }

    public OrderResponseDTO getOrder(GetOrderRequestDTO dto) {

        List<FoodClient> clientsFound = foodClientRepository.findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(dto.getClientSearchName(), dto.getClientSearchName());
        OrderResponseDTO result = null;

        if (clientsFound.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No clients found");

        for (FoodClient client : clientsFound) {
            List<FoodOrder> ordersOpt = foodOrderRepository.findAllByClientId(client.getClientId());
            result =  OrderResponseDTO.builder()
                    .orderResponseData(ordersOpt
                            .stream()
                            .map(x -> clientMapper.orderAndClientToOrderResponseData(x, client))
                            .collect(Collectors.toList()))
                    .build();
        }

        return result;
    }
}
