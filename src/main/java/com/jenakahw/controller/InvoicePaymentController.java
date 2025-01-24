package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.InvoiceHasPayment;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.InvoicePaymentService;

@RestController
@RequestMapping(value = "/invoicepayment") // add class level mapping /invoice
public class InvoicePaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	InvoicePaymentService invoicePaymentService;
	
	@Autowired
	private AuthService authService;
	
	// get mapping for generate invoice payment UI
	@GetMapping
	public ModelAndView getInvoicePaymentUI() {
		SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();


		ModelAndView invoicePaymentView = new ModelAndView();
		invoicePaymentView.addObject("title", "Invoice Payment | Jenaka Hardware");
		invoicePaymentView.addObject("logusername", loggedUser.getUsername());
		invoicePaymentView.addObject("loguserrole", userRole);
		invoicePaymentView.addObject("loguserphoto", loggedUser.getUserPhoto());
		invoicePaymentView.setViewName("paymentinvoice.html");
		return invoicePaymentView;
	}

	// get service mapping for get all customers
	@GetMapping(value = "/findall", produces = "application/json")
	public List<InvoiceHasPayment> findAll() {
		return invoicePaymentService.findAll();

	}

	// post mapping for save new invoice payment
	@PostMapping
	public String saveInvoicePayment(@RequestBody InvoiceHasPayment invoicePayment) {
		return invoicePaymentService.saveInvoicePayment(invoicePayment);
	}

}
