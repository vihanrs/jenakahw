package com.jenakahw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.DailyExtraIncome;
import com.jenakahw.repository.DailyExtraIncomeRepository;
import com.jenakahw.repository.DailyIncomeExpensesStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.DailyExtraIncomeService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class DailyExtraIncomeServiceImpl implements DailyExtraIncomeService {

	// Make it final for immutability
	private final DailyExtraIncomeRepository dailyExtraIncomeRepository;
	private final DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Daily Extra Income";

	// Constructor injection
	public DailyExtraIncomeServiceImpl(DailyExtraIncomeRepository dailyExtraIncomeRepository,
			DailyIncomeExpensesStatusRepository dailyIncomeExpensesStatusRepository, PrivilegeHelper privilegeHelper,
			AuthService authService) {
		this.dailyExtraIncomeRepository = dailyExtraIncomeRepository;
		this.dailyIncomeExpensesStatusRepository = dailyIncomeExpensesStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<DailyExtraIncome> findAll() {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return dailyExtraIncomeRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public String saveDailyExtraIncome(DailyExtraIncome dailyExtraIncome) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExtraIncome.setAddedDateTime(LocalDateTime.now());
			// set added user
			dailyExtraIncome.setAddedUserId(authService.getLoggedUser().getId());

			dailyExtraIncomeRepository.save(dailyExtraIncome);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String updateDailyExtraIncome(DailyExtraIncome dailyExtraIncome) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		try {
			// set added date time
			dailyExtraIncome.setLastUpdatedDateTime(LocalDateTime.now());
			// set added user
			dailyExtraIncome.setUpdatedUserId(authService.getLoggedUser().getId());

			dailyExtraIncomeRepository.save(dailyExtraIncome);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String deleteDailyExtraIncome(DailyExtraIncome dailyExtraIncome) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
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
			dailyExtraIncome.setDeletedUserId(authService.getLoggedUser().getId());

			// set statuts to 'Deleted'
			dailyExtraIncome.setDailyIncomeExpensesStatusId(dailyIncomeExpensesStatusRepository.getReferenceById(2));

			dailyExtraIncomeRepository.save(dailyExtraIncome);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
