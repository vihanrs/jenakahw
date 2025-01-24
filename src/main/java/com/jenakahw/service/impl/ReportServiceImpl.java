package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.ReportDailyFinancialSummary;
import com.jenakahw.domain.ReportGrn;
import com.jenakahw.domain.ReportMonthlyFinancialSummary;
import com.jenakahw.domain.ReportPurchaseOrder;
import com.jenakahw.repository.ReportRepository;
import com.jenakahw.service.interfaces.ReportService;

@Service
public class ReportServiceImpl implements ReportService{
	private final ReportRepository reportRepository; // Make it final for immutability

	// Constructor injection
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

	@Override
	public List<PurchaseOrder> getPurchaseOrderByStatus(int statusId) {
		return reportRepository.purchaseOrderByStatus(statusId);
	}

	@Override
	public List<PurchaseOrder> getPurchaseOrderBySupplier(int supplierId) {
		return reportRepository.purchaseOrderBySupplier(supplierId);
	}

	@Override
	public List<PurchaseOrder> getPurchaseOrderByStatusAndSupplier(int statusId, int supplierId) {
		return reportRepository.purchaseOrderByStatusAndSupplier(statusId, supplierId);
	}

	@Override
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

	@Override
	public List<Grn> getGrnBySupplier(int supplierId) {
		return reportRepository.grnBySupplierId(supplierId);
	}

	@Override
	public List<ReportGrn> getGrnSummaryByMonthly() {
		String[][] queryDataList = reportRepository.grnSummaryByMonthly();
		List<ReportGrn> reportGrns = new ArrayList<>();

		for (String[] queryData : queryDataList) {
			ReportGrn reportGrn = new ReportGrn();
			reportGrn.setAddedMonth(queryData[0]);
			reportGrn.setGrnGrandTotal(queryData[1]);
			reportGrn.setGrnCount(queryData[2]);

			reportGrns.add(reportGrn);
		}

		return reportGrns;
	}

	@Override
	public List<ReportDailyFinancialSummary> getSalesSummaryByDaily() {
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
				// get current total for the day
				BigDecimal total = income.get(queryData[0]);
				income.put(queryData[0], total.add(amount)); // update total
			} else {
				// get current total for the day
				BigDecimal total = expense.get(queryData[0]);
				expense.put(queryData[0], total.add(amount)); // update total
			}
		}

		// loops the days of week and update income/expense values
		for (String day : daysOfWeek) {
			BigDecimal incomeValue = income.get(day);
			BigDecimal expenseValue = expense.get(day);
			ReportDailyFinancialSummary dailySummary = new ReportDailyFinancialSummary(day, incomeValue, expenseValue);
			summary.add(dailySummary);
		}

		return summary;
	}

	@Override
	public List<ReportMonthlyFinancialSummary> getSalesSummaryByMonthly() {
		String[][] queryDataList = reportRepository.getMonthlyFinancialSummary();
		List<ReportMonthlyFinancialSummary> summary = new ArrayList<>();

		String[] monthsOfYear = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		LinkedHashMap<String, BigDecimal> income = new LinkedHashMap<>();
		LinkedHashMap<String, BigDecimal> expense = new LinkedHashMap<>();

		// update LinkedHashMaps with months of the year and BigDecimal.ZERO
		for (String month : monthsOfYear) {
			income.put(month, BigDecimal.ZERO);
			expense.put(month, BigDecimal.ZERO);
		}

		// update LinkedHashMaps with incomes and expenses
		for (String[] queryData : queryDataList) {
			BigDecimal amount = new BigDecimal(queryData[1]);
			if (queryData[2].equals("Invoice") || queryData[2].equals("Extra Income")) {
				// get current total for the month
				BigDecimal total = income.get(queryData[0]);
				income.put(queryData[0], total.add(amount)); // update total
			} else {
				// get current total for the day
				BigDecimal total = expense.get(queryData[0]);
				expense.put(queryData[0], total.add(amount)); // update total
			}
		}

		// loops the months of year and update income/expense values
		for (String month : monthsOfYear) {
			BigDecimal incomeValue = income.get(month);
			BigDecimal expenseValue = expense.get(month);
			ReportMonthlyFinancialSummary monthlySummary = new ReportMonthlyFinancialSummary(month, incomeValue,
					expenseValue);
			summary.add(monthlySummary);
		}
		return summary;
	}
}
