package com.jenakahw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.jenakahw.repository.DailyExpensesRepository;
import com.jenakahw.repository.DailyIncomeExpensesStatusRepository;

@RestController
//add class level mapping /dailyexpenses
@RequestMapping(value = "/dailyexpenses")
public class DailyExpensesController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private DailyExpensesRepository dailyExpensesRepository;

	@Autowired
	private DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Daily Expenses";

	// get mapping for generate daily expenses UI
	@GetMapping
	public ModelAndView getDailyExpensesUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView dailyexpensesView = new ModelAndView();
		dailyexpensesView.addObject("title", "Daily Expenses  | Jenaka Hardware");
		dailyexpensesView.addObject("logusername", auth.getName());
		dailyexpensesView.addObject("loguserrole", userRole);
		dailyexpensesView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dailyexpensesView.setViewName("dailyexpenses.html");
		return dailyexpensesView;
	}

	// get service mapping for get all daily expenses
	@GetMapping(value = "/findall", produces = "application/json")
	public List<DailyExpenses> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return dailyExpensesRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}

	// post mapping for save new daily expense
	@PostMapping
	public String saveDailyExtraIncome(@RequestBody DailyExpenses dailyExpenses) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExpenses.setAddedDateTime(LocalDateTime.now());
			// set added user
			dailyExpenses.setAddedUserId(userController.getLoggedUser().getId());

			dailyExpensesRepository.save(dailyExpenses);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// post mapping for update daily expense
	@PutMapping
	public String updateCustomer(@RequestBody DailyExpenses dailyExpenses) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExpenses.setLastUpdatedDateTime(LocalDateTime.now());
			// set added user
			dailyExpenses.setUpdatedUserId(userController.getLoggedUser().getId());

			dailyExpensesRepository.save(dailyExpenses);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete daily expense
	@DeleteMapping
	public String deleteProduct(@RequestBody DailyExpenses dailyExpenses) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check given expense exist or not
		DailyExpenses extDailyExpenses = dailyExpensesRepository.getReferenceById(dailyExpenses.getId());
		if (extDailyExpenses == null) {
			return "Daily Extra Income Not Exist..!";
		}

		try {
			// set deleted data and time
			dailyExpenses.setDeletedDateTime(LocalDateTime.now());

			// set deleted user id
			dailyExpenses.setDeletedUserId(userController.getLoggedUser().getId());

			// set statuts to 'Deleted'
			dailyExpenses.setDailyIncomeExpensesStatusId(dailyIncomeExpensesStatusRepository.getReferenceById(2));

			dailyExpensesRepository.save(dailyExpenses);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
