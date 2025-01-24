package com.jenakahw.util;

import java.util.HashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jenakahw.repository.PrivilegeRepository;

@Component
public class PrivilegeHelper {
	private final PrivilegeRepository privilegeRepository;

	public PrivilegeHelper(PrivilegeRepository privilegeRepository) {
		this.privilegeRepository = privilegeRepository;
	}

	// method for get privileges by given user and module
	public HashMap<String, Boolean> getPrivilegeByUserAndModule(String module) {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		HashMap<String, Boolean> userPrivilege = new HashMap<String, Boolean>();

		if (auth.getName().equals("admin")) {
			userPrivilege.put("select", true);
			userPrivilege.put("insert", true);
			userPrivilege.put("update", true);
			userPrivilege.put("delete", true);
		} else {
			String userPrivileges = privilegeRepository.getPrivilegesByUserAndModule(auth.getName(), module);
			String[] privileges = userPrivileges.split(",");
			userPrivilege.put("select", privileges[0].equals("1"));
			userPrivilege.put("insert", privileges[1].equals("1"));
			userPrivilege.put("update", privileges[2].equals("1"));
			userPrivilege.put("delete", privileges[3].equals("1"));
		}
		return userPrivilege;
	}

	// method for check privileges for selected operation in selected module
	public Boolean hasPrivilege(String module, String operation) {
		HashMap<String, Boolean> logedUserPrivileges = getPrivilegeByUserAndModule(module);
		return logedUserPrivileges.get(operation);
	}
}
