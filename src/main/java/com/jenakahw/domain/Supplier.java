package com.jenakahw.domain;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // applied as an entity class
@Table(name = "supplier") // map table
@Data //generate getters and setters
@NoArgsConstructor //generate default constructor
@AllArgsConstructor //generate all args constructor
public class Supplier {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "first_name")
	@NotNull
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	@Column(name = "contact")
	@NotNull
	private String contact;
	
	@Column(name = "company")
	private String company;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "email")
	private String email;
	
	@ManyToOne
	@JoinColumn(name = "supplier_status_id", referencedColumnName = "id")
	private SupplierStatus supplierStatusId;
	
	@Column(name = "added_datetime")
	@NotNull
	private LocalDateTime addedDateTime;
	
	@Column(name = "lastupdated_datetime")
	private LocalDateTime lastUpdatedDateTime;
	
	@Column(name = "deleted_datetime")
	private LocalDateTime deletedDateTime;
	
	@ManyToOne // relationship format
	@JoinColumn(name = "user_id", referencedColumnName = "id") // join column condition
	private User userId;
	
	@Column(name = "updated_user_id")
	private Integer updatedUserId;
	
	@Column(name = "deleted_user_id")
	private Integer deletedUserId;
	
	@ManyToMany
	@JoinTable(name = "product_has_supplier", joinColumns = @JoinColumn(name="supplier_id"),inverseJoinColumns = @JoinColumn(name="product_id"))
	private Set<Product> products;
}
