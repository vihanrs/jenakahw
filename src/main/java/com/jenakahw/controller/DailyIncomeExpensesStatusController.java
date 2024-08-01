package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.DailyIncomeExpensesStatus;
import com.jenakahw.repository.DailyIncomeExpensesStatusRepository;

@RestController
@RequestMapping(value = "/dailyincomeexpensesstatus") // class level mapping
public class DailyIncomeExpensesStatusController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired  
	private DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository;
	
	//get mapping for get all daily income expenses statuses -- [/dailyincomeexpensesstatus/findall]
		@GetMapping(value = "/findall", produces = "application/json")
		public List<DailyIncomeExpensesStatus> findAll() {
			return dailyIncomeExpensesStatusRepository.findAll();
		}
}
