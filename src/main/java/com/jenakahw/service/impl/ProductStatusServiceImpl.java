package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.ProductStatus;
import com.jenakahw.repository.ProductStatusRepository;
import com.jenakahw.service.interfaces.ProductStatusService;

@Service
public class ProductStatusServiceImpl implements ProductStatusService {
	// Make it final for immutability
	private final ProductStatusRepository productStatusRepository;

	// Constructor injection
    public ProductStatusServiceImpl(ProductStatusRepository productStatusRepository) {
        this.productStatusRepository = productStatusRepository;
    }
    
    @Override
    public List<ProductStatus> findAll() {
        return productStatusRepository.findAll();
    }
}
