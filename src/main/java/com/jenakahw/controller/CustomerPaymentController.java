package com.jenakahw.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.CustomerPayment;
import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.InvoiceHasPayment;
import com.jenakahw.domain.User;
import com.jenakahw.repository.CustomerPaymentRepository;
import com.jenakahw.repository.InvoicePaymentRepository;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;

@RestController
//add class level mapping /customerpayment
@RequestMapping(value = "/customerpayment")
public class CustomerPaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private CustomerPaymentRepository customerPaymentRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoicePaymentRepository invoicePaymentRepository;

	@Autowired
	private InvoiceStatusRepository invoiceStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Customer Payment";

	// get mapping for generate customer payment UI
	@GetMapping
	public ModelAndView getCustomerPaymentUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView customerPaymentView = new ModelAndView();
		
		customerPaymentView.addObject("logusername", auth.getName());
		customerPaymentView.addObject("loguserrole", userRole);
		customerPaymentView.addObject("loguserphoto", loggedUser.getUserPhoto());
		customerPaymentView.addObject("title", "Customer Payment | Jenaka Hardware");
		customerPaymentView.setViewName("paymentcustomer.html");
		return customerPaymentView;
	}

	// get mapping for get all supplier payments
	@GetMapping(value = "/findall", produces = "application/json")
	public List<CustomerPayment> findAll() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			if (userController.isLoggedUserHasRole("Cashier")) {
				return customerPaymentRepository.findAllByUser(userController.getLoggedUser().getId());
			} else {
				return customerPaymentRepository.findAll(Sort.by(Direction.DESC, "id"));
			}
		} else {
			return null;
		}
	}

	// get mapping for get all supplier payments by added user
	@GetMapping(value = "/findallbyuser/{userid}", produces = "application/json")
	public List<CustomerPayment> findAllByUser(@PathVariable("userid") int userId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return customerPaymentRepository.findAllByUser(userId);
		} else {
			return null;
		}
	}
	
	// get mapping for get all grn payments by supplier payment id
	@GetMapping(value = "/findinvpaymentsbycustomerpayment/{customerpaymentid}", produces = "application/json")
	public List<InvoiceHasPayment> findGrnPaymentsSupplierPayment(@PathVariable("customerpaymentid") int customerPaymentId) {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoicePaymentRepository.findInvPaymentsByCustomerPayment(customerPaymentId);
		} else {
			return null;
		}
	}

	// post mapping for save new customer payment
//	@Transactional
	@PostMapping
	public String saveInvoicePayment(@RequestBody CustomerPayment customerPayment) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// get logged user id
			int loggedUserId = userController.getLoggedUser().getId();

			// set customer payment date and added user
			customerPayment.setAddedDateTime(LocalDateTime.now());
			customerPayment.setAddedUserId(loggedUserId);

			// set next payment invoice id
			String nextPaymentInvCode = customerPaymentRepository.getNextPayInvoiceID();
			if (nextPaymentInvCode == null) {
				// formate current date
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
				String formattedDate = LocalDate.now().format(formatter);

				// create new invoice id for start new date
				nextPaymentInvCode = "INVC" + formattedDate + "001";
			}

			customerPayment.setPaymentInvoiceId(nextPaymentInvCode);

			// save customer payment
			CustomerPayment newCustomerPayment = customerPaymentRepository.save(customerPayment);

			// get paidAmount
			BigDecimal paidAmount = newCustomerPayment.getPaidAmount();

			// select incomplete invoices by customer
			List<Invoice> incompleteInvoices = invoiceRepository
					.findByCustomerAndIncomplete(newCustomerPayment.getCustomer().getId());

			for (Invoice invoice : incompleteInvoices) {

				if (!paidAmount.equals(BigDecimal.ZERO)) {

					BigDecimal invoiceBalance = invoice.getBalanceAmount();

					// Compare invoiceBalance with paidAmount
					int comparison = invoiceBalance.compareTo(paidAmount);

					if (comparison <= 0) {
						// invoiceBalance <= paidAmount (-1/0)

						// update invoice
						invoice.setBalanceAmount(BigDecimal.ZERO); // set 0 for balance amount
						invoice.setPaidAmount(invoice.getPaidAmount().add(invoiceBalance)); // update paid amount
						invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(2)); // set status
																									// 'Completed'
						invoiceRepository.save(invoice);

						// save new invoice has payment record
						InvoiceHasPayment invHasPayment = new InvoiceHasPayment(invoice,
								customerPayment.getPaymethodId(), invoiceBalance, newCustomerPayment.getId(),
								LocalDateTime.now(), loggedUserId);
						invoicePaymentRepository.save(invHasPayment);

						// update remaining paid amount
						paidAmount = paidAmount.subtract(invoiceBalance);

					} else {
						// invoiceBalance > paidAmount (1)

						// update invoice
						invoice.setBalanceAmount(invoiceBalance.subtract(paidAmount));
						invoice.setPaidAmount(invoice.getPaidAmount().add(paidAmount)); // update paid amount
						invoiceRepository.save(invoice);

						// save new invoice has payment record
						InvoiceHasPayment invHasPayment = new InvoiceHasPayment(invoice,
								customerPayment.getPaymethodId(), paidAmount, newCustomerPayment.getId(),
								LocalDateTime.now(), loggedUserId);
						invoicePaymentRepository.save(invHasPayment);

						// update remaining paid amount
						paidAmount = BigDecimal.ZERO;
					}
				} else {
					break;
				}
			}

			return newCustomerPayment.getPaymentInvoiceId();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
