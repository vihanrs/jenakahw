package com.jenakahw.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.jenakahw.domain.ReportPurchaseOrder;
import com.jenakahw.repository.ReportRepository;

@RestController
@RequestMapping(value = "report")
public class ReportDataController {

	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */

	@Autowired
	private ReportRepository reportRepository;
	// Purchase Order Reports

	// [/report/reportpurchaseorder/findbystatus/2]
	// get mapping for get purchase order report by status
	@GetMapping(value = "/reportpurchaseorder/findbystatus/{statusId}", produces = "application/json")
	public List<PurchaseOrder> getPurchaseOrderByStatus(@PathVariable("statusId") int statusId) {
		return reportRepository.purchaseOrderByStatus(statusId);
	}

	// [/report/reportpurchaseorder/findbysupplier/8]
	// get mapping for get purchase order report by supplier
	@GetMapping(value = "/reportpurchaseorder/findbysupplier/{supplierId}", produces = "application/json")
	public List<PurchaseOrder> getPurchaseOrderBySupplier(@PathVariable("supplierId") int supplierId) {
		return reportRepository.purchaseOrderBySupplier(supplierId);
	}

	// [/report/reportpurchaseorder/findbystatusandsupplier/2/8]
	// get mapping for get purchase order report by status and supplier
	@GetMapping(value = "/reportpurchaseorder/findbystatusandsupplier/{statusId}/{supplierId}", produces = "application/json")
	public List<PurchaseOrder> getPurchaseOrderByStatusAndSupplier(@PathVariable("statusId") int statusId,
			@PathVariable("supplierId") int supplierId) {
		return reportRepository.purchaseOrderByStatusAndSupplier(statusId, supplierId);
	}

	// [/report/reportpurchaseorder/findposummarybysupplier/8]
	// get mapping for get purchase order summary report by supplier
	@GetMapping(value = "/reportpurchaseorder/findposupplierwisesummary", produces = "application/json")
	public List<ReportPurchaseOrder> getPurchaseOrderSummaryBySupplier() {
		String[][] queryDataList = reportRepository.purchaseOrderSummaryBysupplier();
		List<ReportPurchaseOrder> reportPOs = new ArrayList<>();

		for (String[] queryData : queryDataList) {
			ReportPurchaseOrder reportPurchaseOrder = new ReportPurchaseOrder();
			reportPurchaseOrder.setSupplierFirstName(queryData[0]);
			reportPurchaseOrder.setCompany(queryData[1]);
			reportPurchaseOrder.setCount(queryData[2]);
			reportPurchaseOrder.setTotal(queryData[3]);

			reportPOs.add(reportPurchaseOrder);
		}
		return reportPOs;
	}

	// GRN Reports

	// [/report/reportgrn/findbysupplier/9]
	// get mapping for get grn by supplier
	@GetMapping(value = "/reportgrn/findbysupplier/{supplierId}", produces = "application/json")
	public List<Grn> getGrnBySupplier(@PathVariable("supplierId") int supplierId) {
		return reportRepository.grnBySupplierId(supplierId);
	}

	// [/report/reportgrn/findgrnsummerybymonthly]
	// get mapping for get grn summery report by monthly
	@GetMapping(value = "/reportgrn/findgrnsummarybymonthly", produces = "application/json")
	public List<ReportGrn> getGrnSummarybyMonthly() {
		String[][] queryDataList = reportRepository.grnSummaryByMonthly();
		List<ReportGrn> reportGrns = new ArrayList<>();

		for (String[] queryData : queryDataList) {
			ReportGrn reportGrn = new ReportGrn();
			reportGrn.setAddedMonth(queryData[0]);
			reportGrn.setGrnGrandTotal(queryData[1]);

			reportGrns.add(reportGrn);
		}

		return reportGrns;
	}

	// Sales Reports
	// [/report/reportsales/dailysummery]
	@GetMapping(value = "/reportsales/dailysummery", produces = "application/json")
	public List<ReportDailyFinancialSummary> getSalesSummarybyDaily() {
		String[][] queryDataList = reportRepository.getDailyFinancialSummary();
		List<ReportDailyFinancialSummary> summary = new ArrayList<>();

		String[] daysOfWeek = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

		LinkedHashMap<String, BigDecimal> income = new LinkedHashMap<>();
		LinkedHashMap<String, BigDecimal> expense = new LinkedHashMap<>();

		// update LinkedHashMaps with days of the week and BigDecimal.ZERO
		for (String day : daysOfWeek) {
			income.put(day, BigDecimal.ZERO);
			expense.put(day, BigDecimal.ZERO);
		}

		// update LinkedHashMaps with incomes and expenses
		for (String[] queryData : queryDataList) {
			BigDecimal amount = new BigDecimal(queryData[2]);
			if (queryData[3].equals("Invoice") || queryData[3].equals("Extra Income")) {
				BigDecimal total = income.get(queryData[0]);
				income.put(queryData[0], total.add(amount));
			} else {
				BigDecimal total = expense.get(queryData[0]);
				expense.put(queryData[0], total.add(amount));
			}
		}

		// loops the days of week and update income expense values
		for (String day : daysOfWeek) {
			BigDecimal incomeValue = income.get(day);
			BigDecimal expenseValue = expense.get(day);
			ReportDailyFinancialSummary dailySummary = new ReportDailyFinancialSummary(day, incomeValue, expenseValue);
			summary.add(dailySummary);
		}

		return summary;
	}
}
