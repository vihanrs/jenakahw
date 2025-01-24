package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.InvoiceHasPayment;

public interface InvoicePaymentService {
	
	List<InvoiceHasPayment> findAll();

    String saveInvoicePayment(InvoiceHasPayment invoicePayment);
}
