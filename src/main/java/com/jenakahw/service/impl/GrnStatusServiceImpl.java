package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.GrnStatus;
import com.jenakahw.repository.GrnStatusRepository;
import com.jenakahw.service.interfaces.GrnStatusService;

@Service
public class GrnStatusServiceImpl implements GrnStatusService{
	
	private final GrnStatusRepository grnStatusRepository; // Make it final for immutability

	// Constructor injection
    public GrnStatusServiceImpl(GrnStatusRepository grnStatusRepository) {
        this.grnStatusRepository = grnStatusRepository;
    }
	@Override
	public List<GrnStatus> findAll() {
		return grnStatusRepository.findAll();
	}

}
