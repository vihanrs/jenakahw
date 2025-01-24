package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.Stock;
import com.jenakahw.domain.TopSellingProduct;
import com.jenakahw.repository.ReportRepository;
import com.jenakahw.repository.StockRepository;
import com.jenakahw.service.interfaces.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {
	// Make it final for immutability
	private final ReportRepository reportRepository;
	private final StockRepository stockRepository;

	// Constructor injection
	public DashboardServiceImpl(ReportRepository reportRepository, StockRepository stockRepository) {
		this.reportRepository = reportRepository;
		this.stockRepository = stockRepository;
	}

	@Override
	public int getActiveSupplierCount() {
		return reportRepository.getactiveSupplierCount1();
	}

	@Override
	public int getCustomerCountSinceLastMonth() {
		return reportRepository.getCustomerCountSinceLastMonth();
	}

	@Override
	public int getPendingPOCount() {
		return reportRepository.getPendingPOCount();
	}

	@Override
	public int getCompletedInvoiceCountSinceLastMonth() {
		return reportRepository.getCompletedInvoiceCountSinceLastMonth();
	}

	@Override
	public int getPendingInvoiceCountToday() {
		return reportRepository.getPendingInvoicesToday();
	}

	@Override
	public String getInvoicesGrandTotalSinceLastMonth() {

//		List<Invoice> invoices = reportRepository.getInvoicesSinceLastMonth();
//		BigDecimal totalSell = BigDecimal.ZERO;
//		for (Invoice inv : invoices) {
//			totalSell = totalSell.add(inv.getGrandTotal());
//		}

		return reportRepository.getInvoicesSinceLastMonth();
	}

	@Override
	public List<Stock> getLowStockProducts() {
		return stockRepository.findAllLowStocks();
	}

	@Override
	public List<TopSellingProduct> getTopSellingProducts() {
		List<TopSellingProduct> sellingProducts = new ArrayList<>();
		String[][] productList = reportRepository.getTopSellingProducts();

		for (String[] product : productList) {
			TopSellingProduct topSellingProduct = new TopSellingProduct();
			topSellingProduct.setName(product[0]);
			topSellingProduct.setBrand(product[1]);
			topSellingProduct.setCategory(product[2]);
			topSellingProduct.setSubCategory(product[3]);
			topSellingProduct.setSellQty(new BigDecimal(product[5]));
			topSellingProduct.setTotalAmount(new BigDecimal(product[6]));

			sellingProducts.add(topSellingProduct);
		}

		return sellingProducts;
	}

	@Override
	public List<TopSellingProduct> getAllSellingProductsWithRol() {
		List<TopSellingProduct> sellingProducts = new ArrayList<>();
		String[][] productList = reportRepository.getAllSellingProducts();

		for (String[] product : productList) {
			TopSellingProduct topSellingProduct = new TopSellingProduct();
			topSellingProduct.setName(product[0]);
			topSellingProduct.setBrand(product[1]);
			topSellingProduct.setCategory(product[2]);
			topSellingProduct.setSubCategory(product[3]);
			topSellingProduct.setSellQty(new BigDecimal(product[5]));
			topSellingProduct.setTotalAmount(new BigDecimal(product[6]));
			topSellingProduct.setProductId(Integer.parseInt(product[7]));
			if (product[8] != null) {
				topSellingProduct.setRol(Integer.parseInt(product[8]));
			} else {
				topSellingProduct.setRol(0);
			}
			sellingProducts.add(topSellingProduct);
		}

		return sellingProducts;
	}

}
