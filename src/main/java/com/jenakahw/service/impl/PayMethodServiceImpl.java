package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.PayMethod;
import com.jenakahw.repository.PayMethodRepository;
import com.jenakahw.service.interfaces.PayMethodService;

@Service
public class PayMethodServiceImpl implements PayMethodService{

	private final PayMethodRepository payMethodRepository; // Make it final for immutability

	// Constructor injection
    public PayMethodServiceImpl(PayMethodRepository payMethodRepository) {
        this.payMethodRepository = payMethodRepository;
    }
    
	@Override
	public List<PayMethod> findAll() {
		return payMethodRepository.findAll();
	}

}
