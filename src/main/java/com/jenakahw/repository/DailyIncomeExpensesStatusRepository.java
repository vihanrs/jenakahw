package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.DailyIncomeExpensesStatus;

@Repository
public interface DailyIncomeExpensesStatusRepository extends JpaRepository<DailyIncomeExpensesStatus, Integer>{

}