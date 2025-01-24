package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.SubCategory;
import com.jenakahw.repository.SubCategoryRepository;
import com.jenakahw.service.interfaces.SubCategoryService;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

	private final SubCategoryRepository subCategoryRepository; // Make it final for immutability

	// Constructor injection
	public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository) {
		this.subCategoryRepository = subCategoryRepository;
	}

	@Override
	public List<SubCategory> findAll() {
		return subCategoryRepository.findAll();
	}

	@Override
	public List<SubCategory> findByCategory(Integer categoryId) {
		return subCategoryRepository.findByCategory(categoryId);
	}

	@Override
	public String saveSubCategory(SubCategory subCategory) {
		// check duplicates
		SubCategory extSubCategory = subCategoryRepository.findByName(subCategory.getName());
		if (extSubCategory != null) {
			return extSubCategory.getName() + " already exist";
		}

		try {
			subCategoryRepository.save(subCategory);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
