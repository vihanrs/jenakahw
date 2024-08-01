package com.jenakahw.domain;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

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
@Table(name = "customer") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class Customer {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "full_name")
	@NotNull
	private String fullName;
	
	@Column(name = "nic")
	private String nic;
	
	@Column(name = "contact")
	@NotNull
	@Length(max = 10)
	private String contact;
	
	@Column(name = "address")
	private String address;
	
	@ManyToOne// relationship format
	@JoinColumn(name = "customer_status_id", referencedColumnName = "id") // join column condition
	private CustomerStatus customerStatusId;

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
