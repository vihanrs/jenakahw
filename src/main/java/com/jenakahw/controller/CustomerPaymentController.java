package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
//add class level mapping /customerpayment
@RequestMapping(value = "/customerpayment")
public class CustomerPaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */

	// get mapping for generate product UI
	@GetMapping
	public ModelAndView getCustomerPaymentUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView customerPaymentView = new ModelAndView();
		customerPaymentView.addObject("title", "Customer Payment | Jenaka Hardware");
		customerPaymentView.addObject("logusername", auth.getName());
		customerPaymentView.setViewName("paymentcustomer.html");
		return customerPaymentView;
	}
}
