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

import com.jenakahw.domain.DailyExpenses;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.DailyExpensesService;

@RestController
//add class level mapping /dailyexpenses
@RequestMapping(value = "/dailyexpenses")
public class DailyExpensesController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private DailyExpensesService dailyExpensesService;

	@Autowired
	private AuthService authService;

	// get mapping for generate daily expenses UI
	@GetMapping
	public ModelAndView getDailyExpensesUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView dailyexpensesView = new ModelAndView();
		dailyexpensesView.addObject("title", "Daily Expenses  | Jenaka Hardware");
		dailyexpensesView.addObject("logusername", loggedUser.getUsername());
		dailyexpensesView.addObject("loguserrole", userRole);
		dailyexpensesView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dailyexpensesView.setViewName("dailyexpenses.html");
		return dailyexpensesView;
	}

	// get service mapping for get all daily expenses
	@GetMapping(value = "/findall", produces = "application/json")
	public List<DailyExpenses> findAll() {
		return dailyExpensesService.findAll();
	}

	// post mapping for save new daily expense
	@PostMapping
	public String saveDailyExtraIncome(@RequestBody DailyExpenses dailyExpenses) {
		return dailyExpensesService.saveDailyExpense(dailyExpenses);
	}

	// post mapping for update daily expense
	@PutMapping
	public String updateCustomer(@RequestBody DailyExpenses dailyExpenses) {
		return dailyExpensesService.updateDailyExpense(dailyExpenses);
	}

	// delete mapping for delete daily expense
	@DeleteMapping
	public String deleteProduct(@RequestBody DailyExpenses dailyExpenses) {
		return dailyExpensesService.deleteDailyExpense(dailyExpenses);
	}
}
