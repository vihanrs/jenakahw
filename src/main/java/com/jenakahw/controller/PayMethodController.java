package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.PayMethod;
import com.jenakahw.service.interfaces.PayMethodService;

@RestController
@RequestMapping(value = "/paymethod") // class level mapping
public class PayMethodController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	PayMethodService payMethodService;

	// get mapping for get all paymethods -- [/paymethod/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<PayMethod> findAll() {
		return payMethodService.findAll();
	}
}
