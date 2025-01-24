package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.CustomerPayment;
import com.jenakahw.domain.InvoiceHasPayment;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.CustomerPaymentService;

import jakarta.transaction.Transactional;

@RestController
//add class level mapping /customerpayment
@RequestMapping(value = "/customerpayment")
public class CustomerPaymentController {
	@Autowired
	private CustomerPaymentService customerPaymentService;
	
	@Autowired
    private AuthService authService;

	// get mapping for generate customer payment UI
	@GetMapping
	public ModelAndView getCustomerPaymentUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView customerPaymentView = new ModelAndView();
		
		customerPaymentView.addObject("logusername", loggedUser.getUsername());
		customerPaymentView.addObject("loguserrole", userRole);
		customerPaymentView.addObject("loguserphoto", loggedUser.getUserPhoto());
		customerPaymentView.addObject("title", "Customer Payment | Jenaka Hardware");
		customerPaymentView.setViewName("paymentcustomer.html");
		return customerPaymentView;
	}

	// get mapping for get all supplier payments
	@GetMapping(value = "/findall", produces = "application/json")
	public List<CustomerPayment> findAll() {
		return customerPaymentService.findAll();
	}

	// get mapping for get all supplier payments by added user
	@GetMapping(value = "/findallbyuser/{userid}", produces = "application/json")
	public List<CustomerPayment> findAllByUser(@PathVariable("userid") int userId) {
		return customerPaymentService.findAllByUser(userId);
	}
	
	// get mapping for get all inv payments by customer payment id
	@GetMapping(value = "/findinvpaymentsbycustomerpayment/{customerpaymentid}", produces = "application/json")
	public List<InvoiceHasPayment> findInvPaymentsByCustomerPayment(@PathVariable("customerpaymentid") int customerPaymentId) {
		return customerPaymentService.findInvPaymentsByCustomerPayment(customerPaymentId);
	}

	// post mapping for save new customer payment
	@Transactional
	@PostMapping
	public String saveCustomerPayment(@RequestBody CustomerPayment customerPayment) {
		return customerPaymentService.saveCustomerPayment(customerPayment);
	}

}
