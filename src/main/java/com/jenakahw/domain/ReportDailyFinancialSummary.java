package com.jenakahw.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDailyFinancialSummary {
String day;
BigDecimal income;
BigDecimal expense;
}
