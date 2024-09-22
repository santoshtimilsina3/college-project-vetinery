package com.example.demo.service.Imp;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PetRepository;
import com.example.demo.service.CustomerService;

@Service
public class CustomerServiceImp implements CustomerService {

	
	public final CustomerRepository customerRepository;
	public final PetRepository petRepository;
	public final ResponseBuilder responseBuilder;
	
	public CustomerServiceImp(CustomerRepository customerRepository,PetRepository petRepository,ResponseBuilder	responseBuilder) {
		this.customerRepository=customerRepository;
		this.petRepository=petRepository;
		this.responseBuilder=responseBuilder;
	}
	
	@Override
	public Page<Customer> GetAllPagination(Pageable pageable){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        
        Page<Customer> page=customerRepository.findAll(PageRequest.of(pageable.getPageNumber(), 
				  pageable.getPageSize(), 
				  Sort.by(Sort.Direction.ASC, "customerid")));
        return page;
	}
	
	@Override
	public List<Customer> findAll(){
		return customerRepository.findAll();
	}
	
	@Override
	public List<Customer> findByFirstname(String name){
		return customerRepository.findByFirstname(name);
	}
	
	@Override
	public List<Customer> findByLastname(String name){
		return customerRepository.findByLastname(name);
	}
	@Override
	public Boolean save(Customer customer) {
		try {
			customerRepository.save(customer);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	@Override
	public Optional<Customer> findById(int customerid){
		return customerRepository.findById(customerid);
	}
	@Override
	public Boolean delete(Customer customer) {
		try {
			petRepository.deleteAll(customer.getPets());
			customer.setPets(null);
			customerRepository.delete(customer);
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public ResponseEntity<ApiResponse> getCustomers() {
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);

		Page<Customer> allCustomers = customerRepository.findAll(pageable);
		return responseBuilder.buildResponse(HttpStatus.OK.value(),"All Customers", allCustomers);
	}

    public Optional<Customer> findByEmail(String email) {
		return customerRepository.findCustomerByEmail(email);
    }
}
