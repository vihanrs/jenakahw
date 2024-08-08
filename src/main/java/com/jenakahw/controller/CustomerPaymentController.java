package com.jenakahw.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.CustomerHasPayment;
import com.jenakahw.domain.Invoice;
import com.jenakahw.repository.CustomerPaymentRepository;
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

		ModelAndView customerPaymentView = new ModelAndView();
		customerPaymentView.addObject("title", "Customer Payment | Jenaka Hardware");
		customerPaymentView.addObject("logusername", auth.getName());
		customerPaymentView.setViewName("paymentcustomer.html");
		return customerPaymentView;
	}

	// post mapping for save new invoice payment
	@PostMapping
	public String saveInvoicePayment(@RequestBody CustomerHasPayment customerPayment) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// get paidAmount
			BigDecimal paidAmount = customerPayment.getPaidAmount();

			// select incomplete invoices by customer
			List<Invoice> incompleteInvoices = invoiceRepository
					.findByCustomerAndIncomplete(customerPayment.getCustomer().getId());

			for (Invoice invoice : incompleteInvoices) {
				if (!paidAmount.equals(BigDecimal.ZERO)) {

					BigDecimal invoiceBalance = invoice.getBalanceAmount();

					// Compare invoiceBalance with paidAmount
					int comparison = invoiceBalance.compareTo(paidAmount);

					if (comparison <= 0) {
						// invoiceBalance < paidAmount (-1/0)

						// update invoice
						invoice.setBalanceAmount(BigDecimal.ZERO); // set 0 for balance amount
						invoice.setPaidAmount(invoice.getPaidAmount().add(invoiceBalance)); // update paid amount
						invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(4)); // set status 'Completed'
						invoiceRepository.save(invoice);

						// save customer payment record for this invoice
						saveCustomerPayment(customerPayment, invoice.getId(), invoiceBalance);

						// update remaining paid amount
						paidAmount = paidAmount.subtract(invoiceBalance);

					} else {
						// invoiceBalance > paidAmount (1)
						
						// update invoice
						invoice.setBalanceAmount(invoiceBalance.subtract(paidAmount)); // set 0 for balance amount
						invoice.setPaidAmount(invoice.getPaidAmount().add(paidAmount)); // update paid amount
						invoiceRepository.save(invoice);
						
						// save customer payment record for this invoice
						saveCustomerPayment(customerPayment, invoice.getId(), paidAmount);
					}
				}
			}

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private void saveCustomerPayment(CustomerHasPayment customerPayment, int invoiceId, BigDecimal invPaidAmount) {
		// set invoice id
		customerPayment.setInvoiceId(invoiceId);
		customerPayment.setPaidAmount(invPaidAmount);

		// set customer payment date and added user
		customerPayment.setAddedDateTime(LocalDateTime.now());
		customerPayment.setAddedUserId(userController.getLoggedUser().getId());

		customerPaymentRepository.save(customerPayment);

	}
}
