package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	// get mapping for get purchase order report by status
	@GetMapping(value = "/activesuppliercount", produces = "application/json")
	public int getActiveSupplierCount() {
		return reportRepository.getactiveSupplierCount();
	}
}
