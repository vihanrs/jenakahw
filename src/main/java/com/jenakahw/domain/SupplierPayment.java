package com.jenakahw.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "supplier_payment") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class SupplierPayment {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "total_paid_amount")
	@NotNull
	private BigDecimal totalPaidAmount;
	
	@Column(name = "total_balance_amount")
	@NotNull
	private BigDecimal totalBalanceAmount;
	
	@Column(name = "last_balance_amount")
	@NotNull
	private BigDecimal lastBalanceAmount;
	
	@ManyToOne
	@JoinColumn(name = "paymethod_id",referencedColumnName = "id")
	private PayMethod payMethodId;
	
	@ManyToOne
	@JoinColumn(name = "supplier_id",referencedColumnName = "id")
	private Supplier supplierId;
	
	@Column(name = "paid_datetime")
	@NotNull
	private LocalDateTime paidDateTime;
}
