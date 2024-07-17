package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.GrnStatus;
import com.jenakahw.repository.GrnStatusRepository;

@RestController
@RequestMapping(value = "/grnstatus")
public class GrnStatusController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private GrnStatusRepository grnStatusRepository;

	// get mapping for get all supplierstatus -- [/supplierstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<GrnStatus> findAll() {
		return grnStatusRepository.findAll();
	}
}
