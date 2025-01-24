package com.jenakahw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.DailyExpenses;
import com.jenakahw.repository.DailyExpensesRepository;
import com.jenakahw.repository.DailyIncomeExpensesStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.DailyExpensesService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class DailyExpensesServiceImpl implements DailyExpensesService {

	// Make it final for immutability
	private final DailyExpensesRepository dailyExpensesRepository;
	private final DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Daily Expenses";

	// Constructor injection
	public DailyExpensesServiceImpl(DailyExpensesRepository dailyExpensesRepository,
			DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository, PrivilegeHelper privilegeHelper,
			AuthService authService) {
		this.dailyExpensesRepository = dailyExpensesRepository;
		this.dailyIncomeExpensesStatusRepository = dailyIncomeExpensesStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<DailyExpenses> findAll() {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return dailyExpensesRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public String saveDailyExpense(DailyExpenses dailyExpenses) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExpenses.setAddedDateTime(LocalDateTime.now());
			// set added user
			dailyExpenses.setAddedUserId(authService.getLoggedUser().getId());

			dailyExpensesRepository.save(dailyExpenses);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String updateDailyExpense(DailyExpenses dailyExpenses) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExpenses.setLastUpdatedDateTime(LocalDateTime.now());
			// set added user
			dailyExpenses.setUpdatedUserId(authService.getLoggedUser().getId());

			dailyExpensesRepository.save(dailyExpenses);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String deleteDailyExpense(DailyExpenses dailyExpenses) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
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
			dailyExpenses.setDeletedUserId(authService.getLoggedUser().getId());

			// set statuts to 'Deleted'
			dailyExpenses.setDailyIncomeExpensesStatusId(dailyIncomeExpensesStatusRepository.getReferenceById(2));

			dailyExpensesRepository.save(dailyExpenses);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
