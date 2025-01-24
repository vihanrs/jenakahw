package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Stock;
import com.jenakahw.domain.TopSellingProduct;

public interface DashboardService {
	int getActiveSupplierCount();
    int getCustomerCountSinceLastMonth();
    int getPendingPOCount();
    int getCompletedInvoiceCountSinceLastMonth();
    int getPendingInvoiceCountToday();
    String getInvoicesGrandTotalSinceLastMonth();
    List<Stock> getLowStockProducts();
    List<TopSellingProduct> getTopSellingProducts();
    List<TopSellingProduct> getAllSellingProductsWithRol();
}
