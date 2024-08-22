package com.jenakahw.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMonthlyFinancialSummary {
	String month;
	BigDecimal income;
	BigDecimal expense;
}
