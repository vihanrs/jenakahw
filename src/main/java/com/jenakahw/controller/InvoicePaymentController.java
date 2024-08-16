package com.jenakahw.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.InvoiceHasPayment;
import com.jenakahw.domain.User;
import com.jenakahw.repository.InvoicePaymentRepository;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;

@RestController
@RequestMapping(value = "/invoicepayment") // add class level mapping /invoice
public class InvoicePaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	InvoicePaymentRepository invoicePaymentRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private InvoiceStatusRepository invoiceStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Invoice Payment";

	// get mapping for generate invoice payment UI
	@GetMapping
	public ModelAndView getInvoicePaymentUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();


		ModelAndView invoicePaymentView = new ModelAndView();
		invoicePaymentView.addObject("title", "Invoice Payment | Jenaka Hardware");
		invoicePaymentView.addObject("logusername", auth.getName());
		invoicePaymentView.addObject("loguserrole", userRole);
		invoicePaymentView.addObject("loguserphoto", loggedUser.getUserPhoto());
		invoicePaymentView.setViewName("paymentinvoice.html");
		return invoicePaymentView;
	}

	// get service mapping for get all customers
	@GetMapping(value = "/findall", produces = "application/json")
	public List<InvoiceHasPayment> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoicePaymentRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}

	// post mapping for save new invoice payment
	@PostMapping
	public String saveInvoicePayment(@RequestBody InvoiceHasPayment invoicePayment) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates payments...
		Invoice invoice = invoiceRepository.getReferenceById(invoicePayment.getInvoiceId().getId());
		if (invoice != null && invoice.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0) {
			return "Already Paid!";
		}
		

		try {
			// update invoice data
			invoice.setDiscount(invoicePayment.getInvoiceId().getDiscount());
			invoice.setGrandTotal(invoicePayment.getInvoiceId().getGrandTotal());
			invoice.setPaidAmount(invoicePayment.getPaidAmount());
			invoice.setBalanceAmount(invoice.getGrandTotal().subtract(invoicePayment.getPaidAmount()));
			invoice.setIsCredit(invoicePayment.getInvoiceId().getIsCredit());
			
			if(invoice.getIsCredit()) {
				// update invoice status to "Incompleted"
				invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(4));
			}else {
				// update invoice status to "Completed"
				invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(2));
			}
			
			// update invoice 
			invoiceRepository.save(invoice);
			
			if(!invoicePayment.getPaidAmount().equals(BigDecimal.ZERO)) {
			
			// set invoice payment date and user
			invoicePayment.setAddedDateTime(LocalDateTime.now());
			invoicePayment.setAddedUserId(userController.getLoggedUser().getId());
			
			invoicePaymentRepository.save(invoicePayment);
			
			}
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
