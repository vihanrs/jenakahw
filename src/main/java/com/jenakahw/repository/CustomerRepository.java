package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	// query for get user by given user name
	@Query(value = "Select c from Customer c where c.contact = ?1")
	public Customer getCustomerByContact(String contact);

}
