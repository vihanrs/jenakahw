package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.SupplierStatus;

@Repository
public interface SupplierStatusRepository extends JpaRepository<SupplierStatus, Integer>{

}
