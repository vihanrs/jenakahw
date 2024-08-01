package com.jenakahw.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // applied as an entity class
@Table(name = "grn") // map table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class Grn {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;

	@Column(name = "grn_code")
	@NotNull
	@Length(max = 12)
	private String grnCode;

	@Column(name = "supplier_id")
	private Integer supplierId;

	@ManyToOne// relationship format
	@JoinColumn(name = "purchase_order_id", referencedColumnName = "id") // join column condition
	private PurchaseOrder purchaseOrderId;

	@Column(name = "supplier_inv_no")
	private String supplierInvId;

	@Column(name = "total")
	@NotNull
	private BigDecimal total;

	@Column(name = "discount")
	private BigDecimal discount;

	@Column(name = "grand_total")
	@NotNull
	private BigDecimal grandTotal;

	@Column(name = "paid")
	@NotNull
	private BigDecimal paid;

	@Column(name = "item_count")
	@NotNull
	private Integer itemCount;

	@Column(name = "note")
	private String note;

	@ManyToOne // relationship format
	@JoinColumn(name = "grn_status_id", referencedColumnName = "id") // join column condition
	private GrnStatus grnStatusId;

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

	@OneToMany(mappedBy = "grnId", cascade = CascadeType.ALL, orphanRemoval = true) // map with grnId foreign key property in GrnHasProduct object,use cascade all to access alloperations in grn_has_product table
	private List<GrnHasProduct> grnHasProducts;

}
