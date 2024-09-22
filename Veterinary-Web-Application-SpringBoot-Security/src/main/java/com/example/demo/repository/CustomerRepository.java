 package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Customer;
import com.example.demo.model.User;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	List<Customer> findByFirstname(String firstname);
	List<Customer> findByLastname(String lastname);

	@Query("select c from Customer  c where c.email= :userMail")
	Optional<Customer> findCustomerByEmail(@Param("userMail") String userMail);
}
