package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.DailyExtraIncome;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.DailyExtraIncomeService;

@RestController
//add class level mapping /dailyextraincome
@RequestMapping(value = "/dailyextraincome")
public class DailyExtraIncomeController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private DailyExtraIncomeService dailyExtraIncomeService;

	@Autowired
	private AuthService authService;

	// get mapping for generate daily extra income UI
	@GetMapping
	public ModelAndView getDailyExtraIncomeUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView dailyextraincomeView = new ModelAndView();
		dailyextraincomeView.addObject("title", "Daily Extra Income  | Jenaka Hardware");
		dailyextraincomeView.addObject("logusername", loggedUser.getUsername());
		dailyextraincomeView.addObject("loguserrole", userRole);
		dailyextraincomeView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dailyextraincomeView.setViewName("dailyextraincome.html");
		return dailyextraincomeView;
	}

	// get service mapping for get all daily extra incomes
	@GetMapping(value = "/findall", produces = "application/json")
	public List<DailyExtraIncome> findAll() {
		return dailyExtraIncomeService.findAll();
	}

	// post mapping for save new daily extra income
	@PostMapping
	public String saveDailyExtraIncome(@RequestBody DailyExtraIncome dailyExtraIncome) {
		return dailyExtraIncomeService.saveDailyExtraIncome(dailyExtraIncome);
	}

	// post mapping for update daily extra income
	@PutMapping
	public String updateCustomer(@RequestBody DailyExtraIncome dailyExtraIncome) {
		return dailyExtraIncomeService.updateDailyExtraIncome(dailyExtraIncome);
	}

	// delete mapping for delete daily extra income
	@DeleteMapping
	public String deleteProduct(@RequestBody DailyExtraIncome dailyExtraIncome) {
		return dailyExtraIncomeService.deleteDailyExtraIncome(dailyExtraIncome);
	}
}
