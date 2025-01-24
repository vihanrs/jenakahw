package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.DailyExtraIncome;

public interface DailyExtraIncomeService {
	
	List<DailyExtraIncome> findAll();
	
    String saveDailyExtraIncome(DailyExtraIncome dailyExtraIncome);
    
    String updateDailyExtraIncome(DailyExtraIncome dailyExtraIncome);
    
    String deleteDailyExtraIncome(DailyExtraIncome dailyExtraIncome);
}
