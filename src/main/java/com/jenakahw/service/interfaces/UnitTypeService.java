package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.UnitType;

public interface UnitTypeService {
	List<UnitType> findAll();
    String saveUnitType(UnitType unitType);
}
