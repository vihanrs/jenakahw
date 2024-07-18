package com.jenakahw.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // applied as an entity class
@Table(name = "stock") // for map with given table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class Stock {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;

	@Column(name = "total_qty")
	@NotNull
	private BigDecimal totalQty;
	
	@Column(name = "available_qty")
	@NotNull
	private BigDecimal availableQty;

	@Column(name = "cost_price")
	@NotNull
	private BigDecimal costPrice;

	@Column(name = "sell_price")
	@NotNull
	private BigDecimal sellPrice;

	@Column(name = "is_active")
	private Boolean isActive;
	
	@ManyToOne // relationship format
	@JoinColumn(name = "product_id", referencedColumnName = "id") // join column condition
	private Product productId; 
	
}
