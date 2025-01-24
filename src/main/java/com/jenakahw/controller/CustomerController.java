package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Customer;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.CustomerService;

@RestController
//add class level mapping /customer
@RequestMapping(value = "/customer")
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthService authService;

	// get mapping for generate customer UI
	@GetMapping
	public ModelAndView getCustomerUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView customerView = new ModelAndView();

		customerView.addObject("logusername", auth.getName());
		customerView.addObject("loguserrole", userRole);
		customerView.addObject("loguserphoto", loggedUser.getUserPhoto());
		customerView.addObject("title", "Customer  | Jenaka Hardware");
		customerView.setViewName("customer.html");
		return customerView;
	}

	// get service mapping for get all customers
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Customer> findAll() {
		return customerService.findAll();
	}

	// get mapping for get customer by contact number
	@GetMapping(value = "/getByContact/{contact}", produces = "application/json")
	public Customer getByContact(@PathVariable("contact") String contact) {
		return customerService.getByContact(contact);
	}

	// get mapping for get customer by status
	@GetMapping(value = "/findbystatus/{status}", produces = "application/json")
	public List<Customer> getByStatus(@PathVariable("status") String status) {
		return customerService.getByStatus(status);
	}

	// post mapping for save new customer
	@PostMapping
	public String saveCustomer(@RequestBody Customer customer) {
		return customerService.saveCustomer(customer);
	}

	// post mapping for update customer
	@PutMapping
	public String updateCustomer(@RequestBody Customer customer) {
		return customerService.updateCustomer(customer);
	}

	// delete mapping for delete customer
	@DeleteMapping
	public String deleteProduct(@RequestBody Customer customer) {
		return customerService.deleteCustomer(customer);
	}
}
