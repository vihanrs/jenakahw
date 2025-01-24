package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.SubCategory;
import com.jenakahw.service.interfaces.SubCategoryService;

@RestController
@RequestMapping(value = "/subcategory") // class level mapping
public class SubCategoryController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private SubCategoryService subCategoryService;

	// get mapping for get all subcategories -- [/subcategory/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<SubCategory> findAll() {
		return subCategoryService.findAll();
	}

	// get mapping for get subcategories by category --
	// [/subcategory/findbycategory?category=category]
	@GetMapping(value = "/findbycategory", params = { "categoryid" }, produces = "application/json")
	public List<SubCategory> findByCategory(@RequestParam("categoryid") Integer categoryId) {
		return subCategoryService.findByCategory(categoryId);
	}
	
	// post mapping for save new sub-category
	@PostMapping
	public String saveBrand(@RequestBody SubCategory subCategory) {
		return subCategoryService.saveSubCategory(subCategory);
	}
}
