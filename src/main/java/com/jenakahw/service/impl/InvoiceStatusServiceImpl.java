package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.InvoiceStatus;
import com.jenakahw.repository.InvoiceStatusRepository;
import com.jenakahw.service.interfaces.InvoiceStatusService;

@Service
public class InvoiceStatusServiceImpl implements InvoiceStatusService{
	// Make it final for immutability
	private final InvoiceStatusRepository invoiceStatusRepository;

	// Constructor injection
    public InvoiceStatusServiceImpl(InvoiceStatusRepository invoiceStatusRepository) {
        this.invoiceStatusRepository = invoiceStatusRepository;
    }

    @Override
    public List<InvoiceStatus> findAll() {
        return invoiceStatusRepository.findAll();
    }
}
