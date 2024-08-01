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
@Table(name = "po_has_product") // for map with given table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class POHasProduct {

	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "purchase_price")
	@NotNull
	private BigDecimal purchasePrice;
	
	@Column(name = "qty")
	@NotNull
	private BigDecimal qty;
	
	@Column(name = "line_amount")
	@NotNull
	private BigDecimal lineAmount;

	@ManyToOne // relationship format
	@JoinColumn(name = "purchase_order_id", referencedColumnName = "id") // join column condition
	@JsonIgnore // ignore this variable/property when receiveing data to prevent infinity/recursion loop 
	private PurchaseOrder purchaseOrderId; 

	@ManyToOne // relationship format
	@JoinColumn(name = "product_id", referencedColumnName = "id") // join column condition
	private Product productId; 
	
	public POHasProduct(int id,BigDecimal purchasePrice,BigDecimal qty) {
		this.id = id;
		this.purchasePrice = purchasePrice;
		this.qty = qty;
	}
}
