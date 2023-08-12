package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.model.Customer;
import org.springframework.http.ResponseEntity;

public interface CustomerService {

	public Page<Customer> GetAllPagination(Pageable pageable);
	public List<Customer> findAll();
	public List<Customer> findByFirstname(String name);
	public List<Customer> findByLastname(String name);
	public Boolean save(Customer customer) ;
	public Optional<Customer> findById(int customerid);
	public Boolean delete(Customer customer);

    ResponseEntity<ApiResponse> getCustomers();
}
