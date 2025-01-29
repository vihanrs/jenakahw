package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Brand;
import com.jenakahw.service.interfaces.BrandService;


@RestController
@RequestMapping(value = "/brand") // class level mapping
public class BrandController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
    private BrandService brandService;
	
	//get mapping for get all brands -- [/brand/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Brand> findAll() {
		return brandService.findAll();
	}
	
	
	// post mapping for save new brand
	@PostMapping
	public String saveBrand(@RequestBody Brand brand) {
		return brandService.saveBrand(brand);
	}
}
