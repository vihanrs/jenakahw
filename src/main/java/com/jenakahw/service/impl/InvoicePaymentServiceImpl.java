package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.InvoiceHasPayment;
import com.jenakahw.repository.InvoicePaymentRepository;
import com.jenakahw.repository.InvoiceRepository;
import com.jenakahw.repository.InvoiceStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.InvoicePaymentService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class InvoicePaymentServiceImpl implements InvoicePaymentService {
	// Make it final for immutability
	private final InvoicePaymentRepository invoicePaymentRepository;
	private final InvoiceRepository invoiceRepository;
	private final InvoiceStatusRepository invoiceStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Invoice Payment";

	// Constructor injection
	public InvoicePaymentServiceImpl(InvoicePaymentRepository invoicePaymentRepository,
			InvoiceRepository invoiceRepository, InvoiceStatusRepository invoiceStatusRepository,
			PrivilegeHelper privilegeHelper, AuthService authService) {
		this.invoicePaymentRepository = invoicePaymentRepository;
		this.invoiceRepository = invoiceRepository;
		this.invoiceStatusRepository = invoiceStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<InvoiceHasPayment> findAll() {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return invoicePaymentRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public String saveInvoicePayment(InvoiceHasPayment invoicePayment) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
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

			if (invoice.getIsCredit()) {
				// update invoice status to "Incompleted"
				invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(4));
			} else {
				// update invoice status to "Completed"
				invoice.setInvoiceStatusId(invoiceStatusRepository.getReferenceById(2));
			}

			// update invoice
			Invoice paidInvoice = invoiceRepository.save(invoice);

			if (!invoicePayment.getPaidAmount().equals(BigDecimal.ZERO)) {

				// set invoice payment date and user
				invoicePayment.setAddedDateTime(LocalDateTime.now());
				invoicePayment.setAddedUserId(authService.getLoggedUser().getId());

				invoicePaymentRepository.save(invoicePayment);

			}

			return paidInvoice.getInvoiceId();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
