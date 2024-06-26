package com.jenakahw.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

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
@Table(name = "purchase_order") // for map with given table
@Data // generate getters and setters
@NoArgsConstructor // generate default constructor
@AllArgsConstructor // generate all args constructor
public class PurchaseOrder {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;
	
	@Column(name = "po_code",unique = true)
	@NotNull
	@Length(max = 9)
	private String poCode;
	
	@Column(name = "required_date")
	@NotNull
	private LocalDate requiredDate;
	
	@Column(name = "total_amount")
	@NotNull
	private BigDecimal totalAmount;
	
	@Column(name = "note")
	@NotNull
	private String note;
	
	@ManyToOne // relationship format
	@JoinColumn(name="supplier_id", referencedColumnName = "id") // join column condition
	private Supplier supplierId;
	
	@ManyToOne // relationship format
	@JoinColumn(name = "purchase_order_status_id", referencedColumnName = "id") // join column condition
	private PurchaseOrderStatus purchaseOrderStatusId; 
	
	@Column(name = "added_datetime")
	@NotNull
	private LocalDateTime addedDateTime;
	
	@Column(name = "lastupdated_datetime")
	private LocalDateTime lastUpdatedDateTime;
	
	@Column(name = "deleted_datetime")
	private LocalDateTime deletedDateTime;
	
	@Column(name = "added_user_id")
	private Integer userId;
	
	@Column(name = "updated_user_id")
	private Integer updatedUserId;
	
	@Column(name = "deleted_user_id")
	private Integer deletedUserId;
	
	@OneToMany(mappedBy = "purchaseOrderId") //map with purchaseOrderId foreign key property in OPHasProduct object
	private List<POHasProduct> poHasProductList;
}
