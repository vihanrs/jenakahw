package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Privilege;
import com.jenakahw.repository.PrivilegeRepository;
import com.jenakahw.service.interfaces.PrivilegeService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

	private final PrivilegeRepository privilegeRepository;
	private final PrivilegeHelper privilegeHelper;

	private static final String MODULE = "Privilege";

	public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository, PrivilegeHelper privilegeHelper) {
		this.privilegeRepository = privilegeRepository;
		this.privilegeHelper = privilegeHelper;
	}

	@Override
	public List<Privilege> findAll() {
		return privilegeRepository.findAll(Sort.by(Direction.DESC, "id"));
	}

	@Override
	public String savePrivilege(Privilege privilege) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// duplicate check
		Privilege extPrivilege = privilegeRepository.getByRoleModule(privilege.getRole().getId(),
				privilege.getModule().getId());
		if (extPrivilege != null) {
			return "Privilege already exist by given role and module";
		}

		try {
			privilegeRepository.save(privilege);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String updatePrivilege(Privilege privilege) {

		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check existing
		Privilege extPrivilege = privilegeRepository.getReferenceById(privilege.getId());
		if (extPrivilege == null) {
			return "Given privilege record not exist...!";
		}

		try {
			privilegeRepository.save(privilege);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String deletePrivilege(Privilege privilege) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}
		// check existing
		Privilege extPrivilege = privilegeRepository.getReferenceById(privilege.getId());
		if (extPrivilege == null) {
			return "Given privilege record not exist...!";
		}
		try {
			// set auto generated values
			extPrivilege.setSel(false);
			extPrivilege.setInst(false);
			extPrivilege.setUpd(false);
			extPrivilege.setDel(false);
			privilegeRepository.save(extPrivilege);
			return "OK";

		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
