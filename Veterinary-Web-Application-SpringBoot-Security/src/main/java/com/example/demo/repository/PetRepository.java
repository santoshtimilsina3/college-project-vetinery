package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Customer;
import com.example.demo.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByCustomer(Customer customer);

    List<Pet> findByType(String type);

    List<Pet> findByName(String name);

    @Query("select p from Pet p Where p.adoptionStatus='AVAILABLE'")
    List<Pet> findByAdoptionStatus();

}
