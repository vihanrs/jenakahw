package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "report") // add class level mapping
public class ReportUIController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	
	@Autowired
	private PrivilegeController privilegeController;
}
