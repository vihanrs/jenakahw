package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.UnitType;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Integer>{

}
