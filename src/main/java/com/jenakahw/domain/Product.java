package com.jenakahw.domain;

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

@Entity
@Table(name = "product") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "name")
	@NotNull
	private String name;
	
	@Column(name = "barcode")
	private String barcode;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "rol")
	private Integer rol;

	@ManyToOne // relationship format
	@JoinColumn(name = "brand_id", referencedColumnName = "id") // join column condition
	private Brand brandId;
	
	@ManyToOne // relationship format
	@JoinColumn(name = "product_status_id", referencedColumnName = "id") // join column condition
	private ProductStatus productStatusId;
	
	@ManyToOne // relationship format
	@JoinColumn(name = "subcategory_id", referencedColumnName = "id") // join column condition
	private SubCategory subCategoryId;
	
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
}
