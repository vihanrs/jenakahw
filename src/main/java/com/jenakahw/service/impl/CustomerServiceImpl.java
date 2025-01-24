package com.jenakahw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Customer;
import com.jenakahw.repository.CustomerRepository;
import com.jenakahw.repository.CustomerStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.CustomerService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerStatusRepository customerStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Customer";

	// Constructor injection
	public CustomerServiceImpl(CustomerRepository customerRepository, CustomerStatusRepository customerStatusRepository,
			PrivilegeHelper privilegeHelper, AuthService authService) {
		this.customerRepository = customerRepository;
		this.customerStatusRepository = customerStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<Customer> findAll() {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return customerRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public Customer getByContact(String contact) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			Customer customer = customerRepository.findByContact(contact);
			if (customer != null) {
				return customer;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public List<Customer> getByStatus(String status) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return customerRepository.findByStatus(status);
		} else {
			return null;
		}
	}

	@Override
	public String saveCustomer(Customer customer) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
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
			customer.setAddedUserId(authService.getLoggedUser().getId());

			customerRepository.save(customer);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String updateCustomer(Customer customer) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
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
			customer.setUpdatedUserId(authService.getLoggedUser().getId());

			customerRepository.save(customer);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String deleteCustomer(Customer customer) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
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
			customer.setDeletedUserId(authService.getLoggedUser().getId());

			// set customer statuts to 'Deleted'
			customer.setCustomerStatusId(customerStatusRepository.getReferenceById(3));

			customerRepository.save(customer);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
