package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.Brand;
import com.jenakahw.repository.BrandRepository;
import com.jenakahw.service.interfaces.BrandService;

@Service
public class BrandServiceImpl implements BrandService {
	
	private final BrandRepository brandRepository; // Make it final for immutability

    // Constructor injection
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }
    
	@Override
	public List<Brand> findAll() {
		return brandRepository.findAll();
	}

	@Override
	public String saveBrand(Brand brand) {
		// check duplicates
		Brand extByName = brandRepository.findBrandByName(brand.getName());
		if (extByName != null) {
			return extByName.getName() + " already exist";
		}

		try {
			brandRepository.save(brand);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
