package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.GrnStatus;

@Repository
public interface GrnStatusRepository extends JpaRepository<GrnStatus, Integer>{

}
