package com.jenakahw.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "invoice") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class Invoice {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "invoice_id")
	@NotNull
	private String invoiceId;
	
	@Column(name = "total")
	@NotNull
	private BigDecimal total;
	
	@Column(name = "discount")
	private BigDecimal discount;
	
	@Column(name = "grand_total")
	@NotNull
	private BigDecimal grandTotal;
	
	@Column(name = "paid_amount")
	@NotNull
	private BigDecimal paidAmount;
	
	@Column(name = "balance_amount")
	@NotNull
	private BigDecimal balanceAmount;
	
	@Column(name = "item_count")
	@NotNull
	private Integer itemCount;
	
	@Column(name = "is_credit")
	@NotNull
	private Boolean isCredit;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id") // join column condition
	private Customer customerId;
	
	@ManyToOne
	@JoinColumn(name = "invoice_status_id", referencedColumnName = "id")
	private InvoiceStatus invoiceStatusId;
	
	@Column(name = "added_datetime")
	@NotNull
	private LocalDateTime addedDateTime;

	@Column(name = "lastupdated_datetime")
	private LocalDateTime lastUpdatedDateTime;

	@Column(name = "deleted_datetime")
	private LocalDateTime deletedDateTime;

	@Column(name = "added_user_id")
	@NotNull
	private Integer addedUserId;

	@Column(name = "updated_user_id")
	private Integer updatedUserId;

	@Column(name = "deleted_user_id")
	private Integer deletedUserId;
	
}
