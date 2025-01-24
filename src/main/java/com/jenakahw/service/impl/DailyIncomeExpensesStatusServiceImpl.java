package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.DailyIncomeExpensesStatus;
import com.jenakahw.repository.DailyIncomeExpensesStatusRepository;
import com.jenakahw.service.interfaces.DailyIncomeExpensesStatusService;

@Service
public class DailyIncomeExpensesStatusServiceImpl implements DailyIncomeExpensesStatusService{
	
	private final DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository; // Make it final for immutability

	// Constructor injection
    public DailyIncomeExpensesStatusServiceImpl(DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository) {
        this.dailyIncomeExpensesStatusRepository = dailyIncomeExpensesStatusRepository;
    }

	@Override
	public List<DailyIncomeExpensesStatus> findAll() {
		return dailyIncomeExpensesStatusRepository.findAll();
	}

}
