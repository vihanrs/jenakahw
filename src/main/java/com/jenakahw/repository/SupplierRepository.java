package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer>{
	
	// query for get all active suppliers
	@Query(value = "Select s from Supplier s where s.supplierStatusId = (Select supstatus from SupplierStatus supstatus where name='Active')")
	public List<Supplier> findActiveSuppliers();

	// query for get supplier by contact
	@Query(value = "Select s from Supplier s where s.contact = ?1")
	public Supplier getSupplierByContact(String contact);
	
	// query for get supplier by email
	@Query(value = "Select s from Supplier s where s.email = ?1")
	public Supplier getSupplierByEmail(String email);
}
