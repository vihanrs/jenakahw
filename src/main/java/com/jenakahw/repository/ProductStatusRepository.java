package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.ProductStatus;

@Repository
public interface ProductStatusRepository extends JpaRepository<ProductStatus, Integer>{

}
