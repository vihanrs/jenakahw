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

import com.jenakahw.domain.DailyExtraIncome;
import com.jenakahw.domain.User;
import com.jenakahw.repository.DailyExtraIncomeRepository;
import com.jenakahw.repository.DailyIncomeExpensesStatusRepository;

@RestController
//add class level mapping /dailyextraincome
@RequestMapping(value = "/dailyextraincome")
public class DailyExtraIncomeController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private DailyExtraIncomeRepository dailyExtraIncomeRepository;
	
	@Autowired
	private DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Daily Extra Income";

	// get mapping for generate daily extra income UI
	@GetMapping
	public ModelAndView getDailyExtraIncomeUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView dailyextraincomeView = new ModelAndView();
		dailyextraincomeView.addObject("title", "Daily Extra Income  | Jenaka Hardware");
		dailyextraincomeView.addObject("logusername", auth.getName());
		dailyextraincomeView.addObject("loguserrole", userRole);
		dailyextraincomeView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dailyextraincomeView.setViewName("dailyextraincome.html");
		return dailyextraincomeView;
	}

	// get service mapping for get all daily extra incomes
	@GetMapping(value = "/findall", produces = "application/json")
	public List<DailyExtraIncome> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return dailyExtraIncomeRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}

	// post mapping for save new daily extra income
	@PostMapping
	public String saveDailyExtraIncome(@RequestBody DailyExtraIncome dailyExtraIncome) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExtraIncome.setAddedDateTime(LocalDateTime.now());
			// set added user
			dailyExtraIncome.setAddedUserId(userController.getLoggedUser().getId());

			dailyExtraIncomeRepository.save(dailyExtraIncome);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// post mapping for update daily extra income
	@PutMapping
	public String updateCustomer(@RequestBody DailyExtraIncome dailyExtraIncome) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExtraIncome.setLastUpdatedDateTime(LocalDateTime.now());
			// set added user
			dailyExtraIncome.setUpdatedUserId(userController.getLoggedUser().getId());

			dailyExtraIncomeRepository.save(dailyExtraIncome);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete daily extra income
	@DeleteMapping
	public String deleteProduct(@RequestBody DailyExtraIncome dailyExtraIncome) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check given extra income exist or not
		DailyExtraIncome extDailyExIncome = dailyExtraIncomeRepository.getReferenceById(dailyExtraIncome.getId());
		if (extDailyExIncome == null) {
			return "Daily Extra Income Not Exist..!";
		}

		try {
			// set deleted data and time
			dailyExtraIncome.setDeletedDateTime(LocalDateTime.now());

			// set deleted user id
			dailyExtraIncome.setDeletedUserId(userController.getLoggedUser().getId());

			// set statuts to 'Deleted'
			dailyExtraIncome.setDailyIncomeExpensesStatusId(dailyIncomeExpensesStatusRepository.getReferenceById(2));

			dailyExtraIncomeRepository.save(dailyExtraIncome);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
