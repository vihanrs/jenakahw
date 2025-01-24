package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.SubCategory;

public interface SubCategoryService {
	List<SubCategory> findAll();
    List<SubCategory> findByCategory(Integer categoryId);
    String saveSubCategory(SubCategory subCategory);
}
