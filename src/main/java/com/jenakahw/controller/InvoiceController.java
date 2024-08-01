package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Invoice;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;

@RestController
//add class level mapping /invoice
@RequestMapping(value = "/invoice")
public class InvoiceController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceStatusRepository invoiceStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Invoice";

	// get mapping for generate invoice UI
	@GetMapping
	public ModelAndView getInvoiceUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView invoiceView = new ModelAndView();
		invoiceView.addObject("title", "Invoice  | Jenaka Hardware");
		invoiceView.addObject("logusername", auth.getName());
		invoiceView.setViewName("invoice.html");
		return invoiceView;
	}

	// get service mapping for get all customers
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Invoice> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}
}
