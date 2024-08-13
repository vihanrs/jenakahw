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
@Table(name = "grn_has_supplier_payment") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class GrnHasSupplierPayment {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "paid_amount")
	@NotNull
	private BigDecimal paidAmount;
	
	@ManyToOne
	@JoinColumn(name = "grn_id",referencedColumnName = "id")
	private Grn grnId;
	
	@ManyToOne
	@JoinColumn(name = "supplier_payment_id",referencedColumnName = "id")
	private SupplierPayment supplierPaymentId;
	
	public GrnHasSupplierPayment(BigDecimal paidAmount,Grn grnId,SupplierPayment supplierPaymentId) {
		this.paidAmount =paidAmount;
		this.grnId = grnId;
		this.supplierPaymentId = supplierPaymentId;
	}
}
