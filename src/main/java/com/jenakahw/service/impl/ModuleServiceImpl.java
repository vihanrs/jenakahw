package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Module;
import com.jenakahw.repository.ModuleRepository;
import com.jenakahw.service.interfaces.ModuleService;

@Service
public class ModuleServiceImpl implements ModuleService {

	private final ModuleRepository moduleRepository; // Make it final for immutability

	// Constructor injection
	public ModuleServiceImpl(ModuleRepository moduleRepository) {
		this.moduleRepository = moduleRepository;
	}

	@Override
	public List<Module> findAll() {
		return moduleRepository.findAll();
	}

	@Override
	public List<Module> getModulesByRoleWithoutPrivileges(Integer roleId) {
		return moduleRepository.getModulesByRoleWithoutPrivileges(roleId);
	}

	@Override
	public String[] getModulesByLoggedUser() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return moduleRepository.getModulesByLogedUser(auth.getName());
	}
}
