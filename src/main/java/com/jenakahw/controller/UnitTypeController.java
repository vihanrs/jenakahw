package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.UnitType;
import com.jenakahw.repository.UnitTypeRepository;

@RestController
@RequestMapping(value = "/unittype") // class level mapping
public class UnitTypeController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private UnitTypeRepository unitTypeRepository;

	// get mapping for get all user data -- [/userstatus/findall]
	@GetMapping(value="/findall",produces="application/json")
	public List<UnitType> findAll() {
		return unitTypeRepository.findAll();
	}
}
