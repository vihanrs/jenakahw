package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.DailyExpenses;

public interface DailyExpensesService {

	List<DailyExpenses> findAll();
	
	String saveDailyExpense(DailyExpenses dailyExpenses);
	
    String updateDailyExpense(DailyExpenses dailyExpenses);
    
    String deleteDailyExpense(DailyExpenses dailyExpenses);
}
