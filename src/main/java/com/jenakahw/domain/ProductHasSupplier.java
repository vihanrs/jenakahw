package com.jenakahw.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_has_supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductHasSupplier {
	@Id
	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product productId;
	
	@Id
	@ManyToOne(optional = false)
	@JoinColumn(name = "supplier_id", referencedColumnName = "id")
	private Supplier supplierId;

}
