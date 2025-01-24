package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Customer;

public interface CustomerService {
	
	List<Customer> findAll();
	
	String saveCustomer(Customer customer);
	
    String updateCustomer(Customer customer);
    
    String deleteCustomer(Customer customer);
    
	Customer getByContact(String contact);
	
	List<Customer> getByStatus(String status);
}
