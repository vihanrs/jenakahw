package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.UnitType;


@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Integer> {
	// query for get unit type by name
	@Query(value = "Select u from UnitType u where u.name=?1")
	public UnitType findUnitTypeByName(String name);
}
