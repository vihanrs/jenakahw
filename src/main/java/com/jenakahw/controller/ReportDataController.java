package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.ReportDailyFinancialSummary;
import com.jenakahw.domain.ReportGrn;
import com.jenakahw.domain.ReportMonthlyFinancialSummary;
import com.jenakahw.domain.ReportPurchaseOrder;
import com.jenakahw.service.interfaces.ReportService;

@RestController
@RequestMapping(value = "report")
public class ReportDataController {

	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */

	@Autowired
	private ReportService reportService ;
	// Purchase Order Reports

	// [/report/reportpurchaseorder/findbystatus/2]
	// get mapping for get purchase order report by status
	@GetMapping(value = "/reportpurchaseorder/findbystatus/{statusId}", produces = "application/json")
	public List<PurchaseOrder> getPurchaseOrderByStatus(@PathVariable("statusId") int statusId) {
		return reportService.getPurchaseOrderByStatus(statusId);
	}

	// [/report/reportpurchaseorder/findbysupplier/8]
	// get mapping for get purchase order report by supplier
	@GetMapping(value = "/reportpurchaseorder/findbysupplier/{supplierId}", produces = "application/json")
	public List<PurchaseOrder> getPurchaseOrderBySupplier(@PathVariable("supplierId") int supplierId) {
		return reportService.getPurchaseOrderBySupplier(supplierId);
	}

	// [/report/reportpurchaseorder/findbystatusandsupplier/2/8]
	// get mapping for get purchase order report by status and supplier
	@GetMapping(value = "/reportpurchaseorder/findbystatusandsupplier/{statusId}/{supplierId}", produces = "application/json")
	public List<PurchaseOrder> getPurchaseOrderByStatusAndSupplier(@PathVariable("statusId") int statusId,
			@PathVariable("supplierId") int supplierId) {
		return reportService.getPurchaseOrderByStatusAndSupplier(statusId, supplierId);
	}

	// [/report/reportpurchaseorder/findposummarybysupplier/8]
	// get mapping for get purchase order summary report by supplier
	@GetMapping(value = "/reportpurchaseorder/findposupplierwisesummary", produces = "application/json")
	public List<ReportPurchaseOrder> getPurchaseOrderSummaryBySupplier() {
		return reportService.getPurchaseOrderSummaryBySupplier();
	}

	// GRN Reports

	// [/report/reportgrn/findbysupplier/9]
	// get mapping for get grn by supplier
	@GetMapping(value = "/reportgrn/findbysupplier/{supplierId}", produces = "application/json")
	public List<Grn> getGrnBySupplier(@PathVariable("supplierId") int supplierId) {
		return reportService.getGrnBySupplier(supplierId);
	}

	// [/report/reportgrn/findgrnsummerybymonthly]
	// get mapping for get grn summery report by monthly
	@GetMapping(value = "/reportgrn/findgrnsummarybymonthly", produces = "application/json")
	public List<ReportGrn> getGrnSummarybyMonthly() {
		return reportService.getGrnSummaryByMonthly();
	}

	// Sales Reports
	// [/report/reportsales/dailysummery]
	@GetMapping(value = "/reportsales/dailysummery", produces = "application/json")
	public List<ReportDailyFinancialSummary> getSalesSummarybyDaily() {
		return reportService.getSalesSummaryByDaily();
	}

	// [/report/reportsales/monthlysummery]
	@GetMapping(value = "/reportsales/monthlysummery", produces = "application/json")
	public List<ReportMonthlyFinancialSummary> getSalesSummarybyMonthly() {
		return reportService.getSalesSummaryByMonthly();
	}
}
