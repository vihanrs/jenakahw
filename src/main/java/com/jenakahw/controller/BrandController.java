package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	
	// post mapping for save new brand
	@PostMapping
	public String saveBrand(@RequestBody Brand brand) {
		// check duplicates
		Brand extByName = brandRepository.findBrandByName(brand.getName());
		if(extByName != null) {
			return extByName.getName()+" already exist";
		}
		
		try {
			brandRepository.save(brand);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
