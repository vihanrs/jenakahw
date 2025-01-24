package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.CustomerStatus;
import com.jenakahw.repository.CustomerStatusRepository;
import com.jenakahw.service.interfaces.CustomerStatusService;

@Service
public class CustomerStatusServiceImpl implements CustomerStatusService {

	private final CustomerStatusRepository customerStatusRepository;

	// Constructor injection
	public CustomerStatusServiceImpl(CustomerStatusRepository customerStatusRepository) {
		this.customerStatusRepository = customerStatusRepository;
	}

	@Override
	public List<CustomerStatus> findAll() {
		return customerStatusRepository.findAll();
	}

}
