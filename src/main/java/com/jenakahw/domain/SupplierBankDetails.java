package com.jenakahw.domain;

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
@Table(name = "supplier_bank_details") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class SupplierBankDetails {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "bank_name")
	@NotNull
	private String bankName;
	
	@Column(name = "branch_name")
	@NotNull
	private String branchName;
	
	@Column(name = "acc_no")
	@NotNull
	private String accNo;
	
	@Column(name = "acc_holder_name")
	@NotNull
	private String accHolderName;
	
	@ManyToOne // relationship format
	@JoinColumn(name = "supplier_id", referencedColumnName = "id") // join column condition
	@JsonIgnore // ignore this variable/property when receiveing data to prevent infinity/recursion loop 
	private Supplier supplierId; 
}
