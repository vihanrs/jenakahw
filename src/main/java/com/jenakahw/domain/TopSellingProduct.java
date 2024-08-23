package com.jenakahw.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProduct {
	private String name;
	private String brand;
	private String category;
	private String subCategory;
	private BigDecimal sellQty;
	private BigDecimal totalAmount;
	private int productId;
	private int rol;
}
