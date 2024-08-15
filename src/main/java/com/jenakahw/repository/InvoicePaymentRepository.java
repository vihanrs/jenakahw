package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.InvoiceHasPayment;

@Repository
public interface InvoicePaymentRepository extends JpaRepository<InvoiceHasPayment, Integer>{

	// query for get invoice payments by customer payment
	@Query(value = "Select invp from InvoiceHasPayment invp where invp.customerPaymentId=?1")
	public List<InvoiceHasPayment> findInvPaymentsByCustomerPayment(int customerPaymentId);
}
