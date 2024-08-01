package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.InvoiceStatus;
import com.jenakahw.repository.InvoiceStatusRepository;

@RestController
@RequestMapping(value = "/invoicestatus")
public class InvoiceStatusController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private InvoiceStatusRepository invoiceStatusRepository;

	// get mapping for get all invoice statuses -- [/invoicestatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<InvoiceStatus> findAll() {
		return invoiceStatusRepository.findAll();
	}
}
