package com.jenakahw.service.impl;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.Product;
import com.jenakahw.repository.ProductRepository;
import com.jenakahw.service.interfaces.RolSettingService;

@Service
public class RolSettingServiceImpl implements RolSettingService{
	private final ProductRepository productRepository; // Make it final for immutability

	// Constructor injection
    public RolSettingServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

	@Override
	public String updateProductROL(String data) {
		data = data.replace("\"", "");

		int productId = Integer.parseInt(data.split(",")[0]);
		System.out.println(productId);
		
		Product product = productRepository.getReferenceById(productId);
		if(product == null) {
			return "Invalid Product..!";
		}

		try {
			product.setRol(Integer.parseInt(data.split(",")[1]));

			productRepository.save(product);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
