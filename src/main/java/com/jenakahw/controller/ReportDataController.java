package com.jenakahw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.ReportGrn;
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

	// [/report/reportpurchaseorder/findbystatus//2]
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
		String[][] queryDataList =  reportRepository.grnSummaryByMonthly();
		List<ReportGrn> reportGrns = new ArrayList<>();
		
		for(String[] queryData : queryDataList) {
			ReportGrn reportGrn = new ReportGrn();
			reportGrn.setAddedMonth(queryData[0]);
			reportGrn.setGrnGrandTotal(queryData[1]);
			
			reportGrns.add(reportGrn);
		}
		
		return reportGrns;
	}
}
