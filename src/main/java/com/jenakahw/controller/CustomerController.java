package com.jenakahw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.jenakahw.repository.CustomerRepository;
import com.jenakahw.repository.CustomerStatusRepository;

@RestController
//add class level mapping /customer
@RequestMapping(value = "/customer")
public class CustomerController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerStatusRepository customerStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Customer";

	// get mapping for generate customer UI
	@GetMapping
	public ModelAndView getCustomerUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView customerView = new ModelAndView();
		customerView.addObject("title", "Customer  | Jenaka Hardware");
		customerView.addObject("logusername", auth.getName());
		customerView.setViewName("customer.html");
		return customerView;
	}

	// get service mapping for get all customers
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Customer> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return customerRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}

	// get mapping for get customer by contact number
	@GetMapping(value = "/getByContact/{contact}", produces = "application/json")
	public Customer getByContact(@PathVariable("contact") String contact) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return customerRepository.findByContact(contact);
		} else {
			return null;
		}
	}

	// post mapping for save new customer
	@PostMapping
	public String saveCustomer(@RequestBody Customer customer) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		Customer extCustomerByContact = customerRepository.findByContact(customer.getContact());
		if (extCustomerByContact != null) {
			return "Contact No Already Exist...!";
		}

		try {
			// set added date time
			customer.setAddedDateTime(LocalDateTime.now());
			// set added user
			customer.setAddedUserId(userController.getLoggedUser().getId());

			customerRepository.save(customer);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// post mapping for update customer
	@PutMapping
	public String updateCustomer(@RequestBody Customer customer) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		Customer extCustomerByContact = customerRepository.findByContact(customer.getContact());
		if (extCustomerByContact != null && customer.getId() != extCustomerByContact.getId()) {
			return "Contact No Already Exist...!";
		}

		try {
			// set added date time
			customer.setLastUpdatedDateTime(LocalDateTime.now());
			// set added user
			customer.setUpdatedUserId(userController.getLoggedUser().getId());

			customerRepository.save(customer);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete customer
	@DeleteMapping
	public String deleteProduct(@RequestBody Customer customer) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check given product exist or not
		Customer extCustomer = customerRepository.getReferenceById(customer.getId());
		if (extCustomer == null) {
			return "Customer Not Exist..!";
		}

		try {
			// set deleted data and time
			customer.setDeletedDateTime(LocalDateTime.now());

			// set deleted user id
			customer.setDeletedUserId(userController.getLoggedUser().getId());

			// set customer statuts to 'Deleted'
			customer.setCustomerStatusId(customerStatusRepository.getReferenceById(3));

			customerRepository.save(customer);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
