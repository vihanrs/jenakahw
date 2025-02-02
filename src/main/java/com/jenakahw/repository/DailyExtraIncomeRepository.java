package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.DailyExtraIncome;

@Repository
public interface DailyExtraIncomeRepository extends JpaRepository<DailyExtraIncome, Integer>{

}
