package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Customer;
import com.example.demo.model.Pet;
import com.example.demo.util.AdoptionStatus;

public interface PetService {
    List<Pet> findAll();

    List<Pet> findByCustomer(Customer customer);

    Boolean save(Pet pet);

    Optional<Pet> findById(Long id);

    Boolean delete(Pet entity);

    void updateAdoptionStatus(Long petId, AdoptionStatus adoptionStatus);
}
