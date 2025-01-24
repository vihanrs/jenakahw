package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.ProductStatus;
import com.jenakahw.service.interfaces.ProductStatusService;

@RestController
@RequestMapping(value = "/productstatus") // class level mapping
public class ProductStatusController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired  
	private ProductStatusService productStatusService;
	
	//get mapping for get all product status -- [/productstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<ProductStatus> findAll() {
		return productStatusService.findAll();
	}
}
