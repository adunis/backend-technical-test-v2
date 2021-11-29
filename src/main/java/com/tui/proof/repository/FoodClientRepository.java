package com.tui.proof.repository;

import com.tui.proof.entity.client.FoodClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodClientRepository extends JpaRepository<FoodClient, Integer> {

    List<FoodClient> findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String name, String surname);

    Optional<FoodClient> findByFirstNameAndLastNameAndPhoneNumber(String clientFirstName, String clientLastName, String clientPhoneNumber);
    Optional<FoodClient> findByFirstName(String firstName);
}


