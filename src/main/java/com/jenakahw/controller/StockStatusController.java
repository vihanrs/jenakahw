package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.StockStatus;
import com.jenakahw.service.interfaces.StockStatusService;

@RestController
@RequestMapping(value = "/stockstatus") // class level mapping
public class StockStatusController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private StockStatusService stockStatusService;

	// get mapping for get all user data -- [/userstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<StockStatus> findAll() {
		return stockStatusService.findAll();
	}
}
