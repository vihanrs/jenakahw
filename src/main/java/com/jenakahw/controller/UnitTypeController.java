package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.UnitType;
import com.jenakahw.service.interfaces.UnitTypeService;

@RestController
@RequestMapping(value = "/unittype") // class level mapping
public class UnitTypeController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private UnitTypeService unitTypeService ;

	// get mapping for get all unit type data -- [/unittype/findall]
	@GetMapping(value="/findall",produces="application/json")
	public List<UnitType> findAll() {
		return unitTypeService.findAll();
	}
	
	// post mapping for save new unit type
	@PostMapping
	public String saveUnitType(@RequestBody UnitType unitType) {
		return unitTypeService.saveUnitType(unitType);
	}
}
