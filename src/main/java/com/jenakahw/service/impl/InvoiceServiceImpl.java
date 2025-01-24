package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Customer;
import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.InvoiceHasProduct;
import com.jenakahw.domain.Stock;
import com.jenakahw.repository.CustomerRepository;
import com.jenakahw.repository.CustomerStatusRepository;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;
import com.jenakahw.repository.StockRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.InvoiceService;
import com.jenakahw.service.interfaces.StockService;
import com.jenakahw.util.PrivilegeHelper;

import jakarta.transaction.Transactional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	// Make it final for immutability
	private final InvoiceRepository invoiceRepository;
	private final InvoiceStatusRepository invoiceStatusRepository;
	private final StockRepository stockRepository;
	private final CustomerRepository customerRepository;
	private final CustomerStatusRepository customerStatusRepository;
	private final StockService stockService;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;
	

	private static final String MODULE = "Invoice";

	// Constructor injection
	public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceStatusRepository invoiceStatusRepository,
			StockRepository stockRepository, CustomerRepository customerRepository,
			CustomerStatusRepository customerStatusRepository,StockService stockService, PrivilegeHelper privilegeHelper,
			AuthService authService) {
		this.invoiceRepository = invoiceRepository;
		this.invoiceStatusRepository = invoiceStatusRepository;
		this.stockRepository = stockRepository;
		this.customerRepository = customerRepository;
		this.customerStatusRepository = customerStatusRepository;
		this.stockService = stockService;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<Invoice> findAll() {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public List<Invoice> findAllInDateRange(String fromDate, String toDate) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findAllInDateRange(fromDate, toDate);
		} else {
			return null;
		}
	}

	@Override
	public List<Invoice> findByStatus(String status) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findByStatus(status);
		} else {
			return null;
		}
	}

	@Override
	public List<Invoice> findByCustomerAndIncomplete(int customerId) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findByCustomerAndIncomplete(customerId);
		} else {
			return null;
		}
	}

	@Override
	public Invoice findByInvoiceId(String invoiceId) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoiceRepository.findByInvoiceId(invoiceId);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public String saveInvoice(Invoice invoice) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
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
			if (invoice.getCustomerId().getFullName() != null) {
				// check customer exsiting
				Customer extCustomer = customerRepository.findByContact(invoice.getCustomerId().getContact());
				if (extCustomer != null) {
					invoice.setCustomerId(extCustomer);
				} else {
					// save new customer
					Customer newCustomer = invoice.getCustomerId();
					newCustomer.setAddedDateTime(LocalDateTime.now());
					newCustomer.setAddedUserId(authService.getLoggedUser().getId());
					newCustomer.setCustomerStatusId(customerStatusRepository.getReferenceById(1));
					Customer SavedCustomer = customerRepository.save(newCustomer);

					// add new saved customer to invoice
					invoice.setCustomerId(SavedCustomer);
				}
			} else {
				invoice.setCustomerId(null);
				System.out.println("No Customer");
			}
			// set added user
			invoice.setAddedUserId(authService.getLoggedUser().getId());

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
				stockService.updateStockStatus(extStock.getId()); // update stock status
			}

			Invoice newInvoice = invoiceRepository.save(invoice);

			return newInvoice.getInvoiceId();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String deleteInvoice(Invoice invoice) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		try {
			// set delete user and datetime
			invoice.setDeletedUserId(authService.getLoggedUser().getId());
			invoice.setDeletedDateTime(LocalDateTime.now());

			// set status to 'Deleted'
			invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(3));

			for (InvoiceHasProduct invoiceHasProduct : invoice.getInvoiceHasProducts()) {
				invoiceHasProduct.setInvoiceId(invoice);

				// add stock back
				Stock extStock = stockRepository.getReferenceById(invoiceHasProduct.getStockId().getId());
				extStock.setAvailableQty(extStock.getAvailableQty().add(invoiceHasProduct.getQty()));
				stockRepository.save(extStock); // update stock
				stockService.updateStockStatus(extStock.getId()); // update stock status
			}

			invoiceRepository.save(invoice);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
