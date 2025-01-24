package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Category;
import com.jenakahw.service.interfaces.CategoryService;

@RestController
@RequestMapping(value = "/category") // class level mapping
public class CategoryController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private CategoryService categoryService;

	// get mapping for get all categories -- [/category/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Category> findAll() {
		return categoryService.findAll();
	}
	
	// post mapping for save new category
	@PostMapping
	public String saveCategory(@RequestBody Category category) {
		return categoryService.saveCategory(category);
	}
}
