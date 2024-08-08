package com.jenakahw.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Invoice;
import com.jenakahw.repository.ReportRepository;

@RestController
@RequestMapping(value = "/dashboard") // add class level mapping
public class DashboardController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */

	@Autowired
	private ReportRepository reportRepository;

	// [/dashboard/activesuppliercount]
	// get mapping for get active supplier count
	@GetMapping(value = "/activesuppliercount", produces = "application/json")
	public int getActiveSupplierCount() {
		return reportRepository.getactiveSupplierCount1();
	}

	// get mapping for get customer count since last month
	@GetMapping(value = "/findcustomercountsincelastmonth")
	public int getCustomerCount() {
		return reportRepository.getCustomerCountSinceLastMonth();
	}

	// get mapping for get pending po count
	@GetMapping(value = "/pendingpocount")
	public int getPendingPOCount() {
		return reportRepository.getPendingPOCount();
	}

	// get mapping for get completed invoice count
	@GetMapping(value = "/completeinvcountsincelastmonth")
	public int getCompleteInvoiceCount() {
		return reportRepository.getCompletedInvoiceCountSinceLastMonth();
	}

	// get mapping for get completed invoice count
	@GetMapping(value = "/pendinginvcounttoday")
	public int getPendingInvoiceCountToday() {
		return reportRepository.getPendingInvoicesToday();
	}

	// get mapping for get completed invoice grand total since last month
	@GetMapping(value = "/invoicetotalsincelastmonth")
	public BigDecimal getInvoicesGrandTotal() {

//		List<Invoice> invoices = reportRepository.getInvoicesSinceLastMonth();
		BigDecimal totalSell = BigDecimal.ZERO;
//		for (Invoice inv : invoices) {
//			totalSell = totalSell.add(inv.getGrandTotal());
//		}

		return totalSell;
	}

}
