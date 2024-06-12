package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Brand;
import com.jenakahw.repository.BrandRepository;

@RestController
@RequestMapping(value = "/brand") // class level mapping
public class BrandController {

	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired  
	private BrandRepository brandRepository;
	
	//get mapping for get all brands -- [/brand/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Brand> findAll() {
		return brandRepository.findAll();
	}
}
