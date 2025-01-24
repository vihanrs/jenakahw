package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.StockStatus;
import com.jenakahw.repository.StockStatusRepository;
import com.jenakahw.service.interfaces.StockStatusService;

@Service
public class StockStatusServiceImpl implements StockStatusService{
	
	private final StockStatusRepository stockStatusRepository; // Make it final for immutability

	// Constructor injection
    public StockStatusServiceImpl(StockStatusRepository stockStatusRepository) {
        this.stockStatusRepository = stockStatusRepository;
    }

    @Override
    public List<StockStatus> findAll() {
        return stockStatusRepository.findAll();
    }
}
