package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.CustomerStatus;
import com.jenakahw.service.interfaces.CustomerStatusService;

@RestController
@RequestMapping(value = "/customerstatus")
public class CustomerStatusController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private CustomerStatusService customerStatusService;

	// get mapping for get all customerstatus -- [/cusotmerstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<CustomerStatus> findAll() {
		return customerStatusService.findAll();
	}
}
