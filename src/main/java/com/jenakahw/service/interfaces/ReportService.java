package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.ReportDailyFinancialSummary;
import com.jenakahw.domain.ReportGrn;
import com.jenakahw.domain.ReportMonthlyFinancialSummary;
import com.jenakahw.domain.ReportPurchaseOrder;

public interface ReportService {
	
	List<PurchaseOrder> getPurchaseOrderByStatus(int statusId);
	
    List<PurchaseOrder> getPurchaseOrderBySupplier(int supplierId);
    
    List<PurchaseOrder> getPurchaseOrderByStatusAndSupplier(int statusId, int supplierId);
    
    List<ReportPurchaseOrder> getPurchaseOrderSummaryBySupplier();
    
    List<Grn> getGrnBySupplier(int supplierId);
    
    List<ReportGrn> getGrnSummaryByMonthly();
    
    List<ReportDailyFinancialSummary> getSalesSummaryByDaily();
    
    List<ReportMonthlyFinancialSummary> getSalesSummaryByMonthly();
}
