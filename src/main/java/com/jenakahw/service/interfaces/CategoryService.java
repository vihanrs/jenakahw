package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Category;

public interface CategoryService {
	
	List<Category> findAll();

	String saveCategory(Category category);
}
