package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Category;
import com.jenakahw.domain.SubCategory;
import com.jenakahw.repository.SubCategoryRepository;

@RestController
@RequestMapping(value = "/subcategory") // class level mapping
public class SubCategoryController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private SubCategoryRepository subCategoryRepository;

	// get mapping for get all subcategories -- [/subcategory/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<SubCategory> findAll() {
		return subCategoryRepository.findAll();
	}

	// get mapping for get subcategories by category --
	// [/subcategory/findbycategory?category=category]
	@GetMapping(value = "/findbycategory", params = { "categoryid" }, produces = "application/json")
	public List<SubCategory> findByCategory(@RequestParam("categoryid") Integer categoryId) {
		return subCategoryRepository.findByCategory(categoryId);
	}
	
	// post mapping for save new sub-category
	@PostMapping
	public String saveBrand(@RequestBody SubCategory subCategory) {
		// check duplicates
		SubCategory extSubCategory = subCategoryRepository.findByName(subCategory.getName());
		if(extSubCategory != null) {
			return extSubCategory.getName()+" already exist";
		}
		
		try {
			subCategoryRepository.save(subCategory);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
