package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.UnitType;
import com.jenakahw.repository.UnitTypeRepository;
import com.jenakahw.service.interfaces.UnitTypeService;

@Service
public class UnitTypeServiceImpl implements UnitTypeService {

	private final UnitTypeRepository unitTypeRepository; // Make it final for immutability

	// Constructor injection
	public UnitTypeServiceImpl(UnitTypeRepository unitTypeRepository) {
		this.unitTypeRepository = unitTypeRepository;
	}

	@Override
	public List<UnitType> findAll() {
		return unitTypeRepository.findAll();
	}

	@Override
	public String saveUnitType(UnitType unitType) {
		// check duplicates
		UnitType extByName = unitTypeRepository.findUnitTypeByName(unitType.getName());
		if (extByName != null) {
			return extByName.getName() + " already exist";
		}

		try {
			unitTypeRepository.save(unitType);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
