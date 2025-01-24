package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.InvoiceService;

@RestController
//add class level mapping /invoice
@RequestMapping(value = "/invoice")
public class InvoiceController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private AuthService authService;

	// get mapping for generate invoice UI
	@GetMapping
	public ModelAndView getInvoiceUI() {
		SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView invoiceView = new ModelAndView();
		invoiceView.addObject("title", "Invoice  | Jenaka Hardware");
		invoiceView.addObject("logusername", loggedUser.getUsername());
		invoiceView.addObject("loguserrole", userRole);
		invoiceView.addObject("loguserphoto", loggedUser.getUserPhoto());
		invoiceView.setViewName("invoice.html");
		return invoiceView;
	}

	// get service mapping for get all invoices
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Invoice> findAll() {
		return invoiceService.findAll();

	}

	// get service mapping for get all invoices in current date
	@GetMapping(value = "/findall/{fromdate}/{todate}", produces = "application/json")
	public List<Invoice> findAllInDateRange(@PathVariable("fromdate") String fromDate,
			@PathVariable("todate") String toDate) {
		return invoiceService.findAllInDateRange(fromDate, toDate);

	}

	// get mapping for find invoices by status
	@GetMapping(value = "/findbystatus/{status}", produces = "application/json")
	public List<Invoice> findByStatus(@PathVariable("status") String status) {
		return invoiceService.findByStatus(status);
	}

	// get mapping for find invoices by status
	@GetMapping(value = "/findincompletebycustomer/{customerid}", produces = "application/json")
	public List<Invoice> findByCustomerAndIncomplete(@PathVariable("customerid") int customerId) {
		return invoiceService.findByCustomerAndIncomplete(customerId);
	}

	// get mapping for find invoices by invoice id
	@GetMapping(value = "/findbyid/{invoiceid}", produces = "application/json")
	public Invoice findByInvoiceId(@PathVariable("invoiceid") String invoiceId) {
		return invoiceService.findByInvoiceId(invoiceId);
	}

	@PostMapping
	public String saveInvoice(@RequestBody Invoice invoice) {
		return invoiceService.saveInvoice(invoice);
	}

	@DeleteMapping
	public String deleteInvoice(@RequestBody Invoice invoice) {
		return invoiceService.deleteInvoice(invoice);
	}
}
