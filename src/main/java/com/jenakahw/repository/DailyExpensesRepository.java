package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.DailyExpenses;

@Repository
public interface DailyExpensesRepository extends JpaRepository<DailyExpenses, Integer>{

}
