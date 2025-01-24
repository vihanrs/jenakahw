package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Brand;

public interface BrandService {

	List<Brand> findAll();
	
	String saveBrand(Brand brand);
}
