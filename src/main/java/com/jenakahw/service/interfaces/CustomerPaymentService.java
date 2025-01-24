package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.CustomerPayment;
import com.jenakahw.domain.InvoiceHasPayment;

public interface CustomerPaymentService {

	List<CustomerPayment> findAll();
	
	List<CustomerPayment> findAllByUser(int userId);
	
	List<InvoiceHasPayment> findInvPaymentsByCustomerPayment(int customerPaymentId);
	
	String saveCustomerPayment(CustomerPayment customerPayment);
}
