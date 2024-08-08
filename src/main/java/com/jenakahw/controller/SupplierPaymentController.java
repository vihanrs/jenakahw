package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.repository.SupplierPaymentRepository;

@RestController
//add class level mapping /supplierpayment
@RequestMapping(value = "/supplierpayment")
public class SupplierPaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired 
	private SupplierPaymentRepository supplierPaymentRepository;
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private PrivilegeController privilegeController;

	private static final String MODULE = "Supplier Payment";
	
	// get mapping for generate product UI
	@GetMapping
	public ModelAndView getProductUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView supplierPaymentView = new ModelAndView();
		supplierPaymentView.addObject("title", "Supplier Payment  | Jenaka Hardware");
		supplierPaymentView.addObject("logusername", auth.getName());
		supplierPaymentView.setViewName("paymentsupplier.html");
		return supplierPaymentView;
	}
}
