package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	// query for get customer by given contact
	@Query(value = "Select c from Customer c where c.contact = ?1")
	public Customer findByContact(String contact);
	
	// query for get customer by status
	@Query(value = "Select c from Customer c where c.customerStatusId = (select cs from CustomerStatus cs where cs.name=?1)")
	public List<Customer> findByStatus(String status);	

}
