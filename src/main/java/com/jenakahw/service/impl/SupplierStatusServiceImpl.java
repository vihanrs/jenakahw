package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.SupplierStatus;
import com.jenakahw.repository.SupplierStatusRepository;
import com.jenakahw.service.interfaces.SupplierStatusService;

@Service
public class SupplierStatusServiceImpl implements SupplierStatusService{
	private final SupplierStatusRepository supplierStatusRepository; // Make it final for immutability

	// Constructor injection
    public SupplierStatusServiceImpl(SupplierStatusRepository supplierStatusRepository) {
        this.supplierStatusRepository = supplierStatusRepository;
    }

    @Override
    public List<SupplierStatus> findAll() {
        return supplierStatusRepository.findAll();
    }
}
