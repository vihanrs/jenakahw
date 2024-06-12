package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.SubCategory;
import com.jenakahw.repository.SubCategoryRepository;

@RestController
@RequestMapping(value = "/subcategory") // class level mapping
public class SubCategoryController{
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired  
	private SubCategoryRepository subCategoryRepository;
	
	//get mapping for get all subcategories -- [/subcategory/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<SubCategory> findAll() {
		return subCategoryRepository.findAll();
	}
}
