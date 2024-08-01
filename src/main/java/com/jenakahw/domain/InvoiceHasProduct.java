package com.jenakahw.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "invoice_has_product") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class InvoiceHasProduct {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "invoice_id",referencedColumnName = "id")
	@JsonIgnore // ignore this variable/property when receiveing data to prevent infinity/recursion loop 
	private Invoice invoiceId;
	
	@ManyToOne
	@JoinColumn(name = "stock_id",referencedColumnName = "id")
	private Stock stockId;
	
	@Column(name = "product_id")
	@NotNull
	private Integer productId;
	
	@Column(name = "qty")
	@NotNull
	private BigDecimal qty;
	
	@Column(name = "sell_price")
	@NotNull
	private BigDecimal sellPrice;
	
	@Column(name = "line_amount")
	@NotNull
	private BigDecimal lineAmount;
}
