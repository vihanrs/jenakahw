package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Brand;
import com.jenakahw.domain.Category;
import com.jenakahw.repository.CategoryRepository;

@RestController
@RequestMapping(value = "/category") // class level mapping
public class CategoryController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private CategoryRepository categoryRepository;

	// get mapping for get all categories -- [/category/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}
	
	// post mapping for save new category
	@PostMapping
	public String saveBrand(@RequestBody Category category) {
		// check duplicates
		Category extCategory = categoryRepository.findByName(category.getName());
		if(extCategory != null) {
			return extCategory.getName()+" already exist";
		}
		
		try {
			categoryRepository.save(category);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
