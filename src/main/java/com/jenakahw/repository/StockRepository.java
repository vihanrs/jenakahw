package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>{

}
