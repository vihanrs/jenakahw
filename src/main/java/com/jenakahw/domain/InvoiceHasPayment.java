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
@Table(name = "invoice_has_payment") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class InvoiceHasPayment {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "invoice_id",referencedColumnName = "id")
	private Invoice invoiceId;
	
	@ManyToOne
	@JoinColumn(name = "paymethod_id",referencedColumnName = "id")
	private PayMethod paymethodId;
	
	@Column(name = "paid_amount")
	@NotNull
	private BigDecimal paidAmount;
	
	@Column(name = "customer_payment_id")
	private Integer customerPaymentId;
	
	@Column(name = "added_datetime")
	@NotNull
	private LocalDateTime addedDateTime;
	
	@Column(name = "added_user_id")
	@NotNull
	private Integer addedUserId;
	
	public InvoiceHasPayment(Invoice invoiceId,PayMethod paymethodId,BigDecimal paidAmount,Integer customerPaymentId,LocalDateTime addedDateTime,Integer addedUserId) {
		this.invoiceId = invoiceId;
		this.paymethodId = paymethodId;
		this.paidAmount = paidAmount;
		this.customerPaymentId = customerPaymentId;
		this.addedDateTime = addedDateTime;
		this.addedUserId = addedUserId;
	}
}
