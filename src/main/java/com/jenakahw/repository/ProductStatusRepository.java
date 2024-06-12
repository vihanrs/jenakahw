package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jenakahw.domain.ProductStatus;

public interface ProductStatusRepository extends JpaRepository<ProductStatus, Integer>{

}
