package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Privilege;

public interface PrivilegeService {
	
	List<Privilege> findAll();
	
	String savePrivilege(Privilege privilege);
	
	String updatePrivilege(Privilege privilege);
	
	String deletePrivilege(Privilege privilege);
}
