package com.tui.proof.repository;

import com.tui.proof.entity.order.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Integer> {

    List<FoodOrder> findAllByClientId(Integer clientId);
}

