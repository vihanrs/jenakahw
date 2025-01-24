package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.CustomerPayment;
import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.InvoiceHasPayment;
import com.jenakahw.repository.CustomerPaymentRepository;
import com.jenakahw.repository.InvoicePaymentRepository;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.CustomerPaymentService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class CustomerPaymentServiceImpl implements CustomerPaymentService {

	private final CustomerPaymentRepository customerPaymentRepository;
	private final InvoiceRepository invoiceRepository;
	private final InvoicePaymentRepository invoicePaymentRepository;
	private final InvoiceStatusRepository invoiceStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Customer Payment";

	// Constructor injection
	public CustomerPaymentServiceImpl(CustomerPaymentRepository customerPaymentRepository,
			InvoiceRepository invoiceRepository, InvoicePaymentRepository invoicePaymentRepository,
			InvoiceStatusRepository invoiceStatusRepository, PrivilegeHelper privilegeHelper, AuthService authService) {
		this.customerPaymentRepository = customerPaymentRepository;
		this.invoiceRepository = invoiceRepository;
		this.invoicePaymentRepository = invoicePaymentRepository;
		this.invoiceStatusRepository = invoiceStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<CustomerPayment> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			if (authService.isLoggedUserHasRole("Cashier")) {
				return customerPaymentRepository.findAllByUser(authService.getLoggedUser().getId());
			} else {
				return customerPaymentRepository.findAll(Sort.by(Direction.DESC, "id"));
			}
		} else {
			return null;
		}
	}

	@Override
	public List<CustomerPayment> findAllByUser(int userId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return customerPaymentRepository.findAllByUser(userId);
		} else {
			return null;
		}
	}

	@Override
	public List<InvoiceHasPayment> findInvPaymentsByCustomerPayment(int customerPaymentId) {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoicePaymentRepository.findInvPaymentsByCustomerPayment(customerPaymentId);
		} else {
			return null;
		}
	}

	@Override
	public String saveCustomerPayment(CustomerPayment customerPayment) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// get logged user id
			int loggedUserId = authService.getLoggedUser().getId();

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
