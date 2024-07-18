package com.jenakahw.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>{

	@Query(value = "select s from Stock s where s.productId.id = ?1 and s.costPrice = ?2")
	public Stock getByProductAndPrice(Integer productId,BigDecimal costPrice);
}
