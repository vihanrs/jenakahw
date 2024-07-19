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
	@NotNull
	private String barcode;

	@Column(name = "description")
	private String description;

	@Column(name = "location")
	private String location;

	@Column(name = "rol")
	private Integer rol;

	@Column(name = "profit_rate")
	@NotNull
	private BigDecimal profitRate;

	@ManyToOne // relationship format
	@JoinColumn(name = "brand_id", referencedColumnName = "id") // join column condition
	private Brand brandId;

	@ManyToOne // relationship format
	@JoinColumn(name = "subcategory_id", referencedColumnName = "id") // join column condition
	private SubCategory subCategoryId;

	@ManyToOne // relationship format
	@JoinColumn(name = "product_status_id", referencedColumnName = "id") // join column condition
	private ProductStatus productStatusId;

	@ManyToOne // relationship format
	@JoinColumn(name = "unit_type_id", referencedColumnName = "id") // join column condition
	private UnitType unitTypeId;

	@Column(name = "added_datetime")
	@NotNull
	private LocalDateTime addedDateTime;

	@Column(name = "lastupdated_datetime")
	private LocalDateTime lastUpdatedDateTime;

	@Column(name = "deleted_datetime")
	private LocalDateTime deletedDateTime;

	@Column(name = "added_user_id")
	private Integer addedUserId;

	@Column(name = "updated_user_id")
	private Integer updatedUserId;

	@Column(name = "deleted_user_id")
	private Integer deletedUserId;

	// parameterised constructor
	public Product(Integer id, String name, String barcode) {
		this.id = id;
		this.name = name;
		this.barcode = barcode;
	}
	
	// parameterised constructor
		public Product(Integer id, String name, String barcode,UnitType type) {
			this.id = id;
			this.name = name;
			this.barcode = barcode;
			this.unitTypeId=type;
		}
}
