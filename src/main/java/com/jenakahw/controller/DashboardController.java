package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Stock;
import com.jenakahw.domain.TopSellingProduct;
import com.jenakahw.service.interfaces.DashboardService;

@RestController
@RequestMapping(value = "/dashboard") // add class level mapping
public class DashboardController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private DashboardService dashboardService;

	// [/dashboard/activesuppliercount]
	// get mapping for get active supplier count
	@GetMapping(value = "/activesuppliercount", produces = "application/json")
	public int getActiveSupplierCount() {
		return dashboardService.getActiveSupplierCount();
	}

	// get mapping for get customer count since last month
	@GetMapping(value = "/findcustomercountsincelastmonth")
	public int getCustomerCount() {
		return dashboardService.getCustomerCountSinceLastMonth();
	}

	// get mapping for get pending po count
	@GetMapping(value = "/pendingpocount")
	public int getPendingPOCount() {
		return dashboardService.getPendingPOCount();
	}

	// get mapping for get completed invoice count
	@GetMapping(value = "/completeinvcountsincelastmonth")
	public int getCompleteInvoiceCount() {
		return dashboardService.getCompletedInvoiceCountSinceLastMonth();
	}

	// get mapping for get completed invoice count
	@GetMapping(value = "/pendinginvcounttoday")
	public int getPendingInvoiceCountToday() {
		return dashboardService.getPendingInvoiceCountToday();
	}

	// get mapping for get completed invoice grand total since last month
	@GetMapping(value = "/invoicetotalsincelastmonth")
	public String getInvoicesGrandTotal() {
		return dashboardService.getInvoicesGrandTotalSinceLastMonth();
	}

	// get mappinng for get low stocks
	@GetMapping(value = "/findlowstockproducts")
	public List<Stock> getAlllowStocks() {
		return dashboardService.getLowStockProducts();
	}

	// get mapping for get top 5 selling products in last 3 months
	@GetMapping(value = "/topsellingproducts")
	public List<TopSellingProduct> getTopSellingProducts() {
		return dashboardService.getTopSellingProducts();
	}

	// get mapping for get top 5 selling products in last 3 months
	@GetMapping(value = "/allsellingproductswithnrol")
	public List<TopSellingProduct> getAllSellingProductsWithRol() {
		return dashboardService.getAllSellingProductsWithRol();
	}

}
