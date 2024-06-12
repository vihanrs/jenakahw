package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.ProductStatus;
import com.jenakahw.repository.ProductStatusRepository;

@RestController
@RequestMapping(value = "/productstatus") // class level mapping
public class ProductStatusController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired  
	private ProductStatusRepository productStatusRepository;
	
	//get mapping for get all brands -- [/productstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<ProductStatus> findAll() {
		return productStatusRepository.findAll();
	}
}
