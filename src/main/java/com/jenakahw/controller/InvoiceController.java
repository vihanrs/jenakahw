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

import com.jenakahw.domain.Customer;
import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.InvoiceHasProduct;
import com.jenakahw.domain.Stock;
import com.jenakahw.repository.CustomerRepository;
import com.jenakahw.repository.CustomerStatusRepository;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;
import com.jenakahw.repository.StockRepository;

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

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerStatusRepository customerStatusRepository;

	@Autowired
	private StockRepository stockRepository;

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

	// get service mapping for get all invoices
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Invoice> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}
	
	// get service mapping for get all invoices in current date
		@GetMapping(value = "/findall/{fromdate}/{todate}", produces = "application/json")
		public List<Invoice> findAllInDateRange(@PathVariable("fromdate") String fromDate,@PathVariable("todate") String toDate) {
			// check privileges
			if (privilegeController.hasPrivilege(MODULE, "select")) {
				return invoiceRepository.findAllInDateRange(fromDate,toDate);
		} else {
				return null;
			}

		}

	// get mapping for find invoices by status
	@GetMapping(value = "/findbystatus/{status}", produces = "application/json")
	public List<Invoice> findByStatus(@PathVariable("status") String status) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findByStatus(status);
		} else {
			return null;
		}
	}

	// get mapping for find invoices by status
	@GetMapping(value = "/findincompletebycustomer/{customerid}", produces = "application/json")
	public List<Invoice> findByCustomerAndIncomplete(@PathVariable("customerid") int customerId) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findByCustomerAndIncomplete(customerId);
		} else {
			return null;
		}
	}

	// get mapping for find invoices by invoice id
	@GetMapping(value = "/findbyid/{invoiceid}", produces = "application/json")
	public Invoice findByInvoiceId(@PathVariable("invoiceid") String invoiceId) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findByInvoiceId(invoiceId);
		} else {
			return null;
		}
	}

	@PostMapping
	public String saveInvoice(@RequestBody Invoice invoice) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check stock availability
		for (InvoiceHasProduct invoiceHasProduct : invoice.getInvoiceHasProducts()) {
			Stock extStock = stockRepository.getReferenceById(invoiceHasProduct.getStockId().getId());
			if (extStock.getAvailableQty().compareTo(invoiceHasProduct.getQty()) < 0) {
				return "Insufficient Stock : " + invoiceHasProduct.getStockId().getProductId().getName() + " - Rs."
						+ invoiceHasProduct.getStockId().getSellPrice();
			}

		}

		try {
			// check customer
			if (invoice.getCustomerId().getId() != null) {
				// check customer exsiting
				Customer extCustomer = customerRepository.findByContact(invoice.getCustomerId().getContact());
				if (extCustomer != null) {
					invoice.setCustomerId(extCustomer);
				} else {
					// save new customer
					Customer newCustomer = invoice.getCustomerId();
					newCustomer.setAddedDateTime(LocalDateTime.now());
					newCustomer.setAddedUserId(userController.getLoggedUser().getId());
					newCustomer.setCustomerStatusId(customerStatusRepository.getReferenceById(1));
					Customer SavedCustomer = customerRepository.save(newCustomer);

					// add new saved customer to invoice
					invoice.setCustomerId(SavedCustomer);
				}
			}else {
				invoice.setCustomerId(null);
				System.out.println("No Customer");
			}
			// set added user
			invoice.setAddedUserId(userController.getLoggedUser().getId());

			// set added date time
			invoice.setAddedDateTime(LocalDateTime.now());

			// set next invoice Id
			String nextInvCode = invoiceRepository.getNextInvoiceID();
			if (nextInvCode == null) {
				// formate current date
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
				String formattedDate = LocalDate.now().format(formatter);

				// create new invoice id for start new date
				nextInvCode = "INV" + formattedDate + "001";
			}

			invoice.setInvoiceId(nextInvCode);
			invoice.setDiscount(new BigDecimal("0"));
			invoice.setGrandTotal(invoice.getTotal());
			invoice.setPaidAmount(new BigDecimal("0"));
			invoice.setBalanceAmount(invoice.getTotal());
			invoice.setIsCredit(false);

			for (InvoiceHasProduct invoiceHasProduct : invoice.getInvoiceHasProducts()) {
				// set invoice id in invoice has product
				invoiceHasProduct.setInvoiceId(invoice);

				// substract the stock
				Stock extStock = stockRepository.getReferenceById(invoiceHasProduct.getStockId().getId());
				extStock.setAvailableQty(extStock.getAvailableQty().subtract(invoiceHasProduct.getQty()));

				stockRepository.save(extStock); // update stock
			}

			invoiceRepository.save(invoice);

			// balance the stocks

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}

	}

}
