package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.Role;
import com.jenakahw.repository.RoleRepository;
import com.jenakahw.service.interfaces.RoleService;

@Service
public class RoleServiceImpl implements RoleService{
	
	private final RoleRepository roleRepository; // Make it final for immutability

	// Constructor injection
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
