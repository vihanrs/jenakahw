package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.Category;
import com.jenakahw.repository.CategoryRepository;
import com.jenakahw.service.interfaces.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository; // Make it final for immutability

	// Constructor injection
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public String saveCategory(Category category) {
		// check duplicates
		Category extCategory = categoryRepository.findByName(category.getName());
		if (extCategory != null) {
			return extCategory.getName() + " already exist";
		}

		try {
			categoryRepository.save(category);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
