package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Role;
import com.jenakahw.repository.RoleRepository;

@RestController
@RequestMapping(value = "/role") // class level mapping
public class RoleController {

	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired 
	private RoleRepository roleRepository;

	// get mapping for get all user data -- [/user/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Role> findAll() {
		return roleRepository.findAll();
	}
}
