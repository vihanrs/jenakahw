package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.SupplierStatus;
import com.jenakahw.repository.SupplierStatusRepository;

@RestController
@RequestMapping(value = "/supplierstatus")
public class SupplierStatusController {
	
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired
	private SupplierStatusRepository supplierStatusRepository;
	
	//get mapping for get all supplierstatus -- [/supplierstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<SupplierStatus> findAll(){
		return supplierStatusRepository.findAll();
	}
}
