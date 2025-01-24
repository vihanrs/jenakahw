package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.PurchaseOrderStatus;
import com.jenakahw.repository.PurchaseOrderStatusRepository;
import com.jenakahw.service.interfaces.PurchaseOrderStatusService;

@Service
public class PurchaseOrderStatusServiceImpl implements PurchaseOrderStatusService{
	private final PurchaseOrderStatusRepository purchaseOrderStatusRepository; // Make it final for immutability

	// Constructor injection
    public PurchaseOrderStatusServiceImpl(PurchaseOrderStatusRepository purchaseOrderStatusRepository) {
        this.purchaseOrderStatusRepository = purchaseOrderStatusRepository;
    }

    @Override
    public List<PurchaseOrderStatus> findAll() {
        return purchaseOrderStatusRepository.findAll();
    }
}
