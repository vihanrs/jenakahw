package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.PurchaseOrder;

import lombok.val;

@Repository
public interface ReportRepository extends JpaRepository<PurchaseOrder, Integer> {

	// purchase order reports query 
	
	// query for get purchase order by status
	@Query(value = "Select p from PurchaseOrder p where p.purchaseOrderStatusId.id = ?1 ")
	public List<PurchaseOrder> purchaseOrderByStatus(int statusId);

	// query for get purchase order by supplier
	@Query(value = "Select p from PurchaseOrder p where p.supplierId.id = ?1 ")
	public List<PurchaseOrder> purchaseOrderBySupplier(int supplierId);

	// query for get purchase order by status and supplier
	@Query(value = "Select p from PurchaseOrder p where p.purchaseOrderStatusId.id = ?1 and supplierId.id=?2")
	public List<PurchaseOrder> purchaseOrderByStatusAndSupplier(int statusId, int supplierId);
	
	// query for get ACTIVE supplier count
	@Query(value = "Select count(s.id) from Supplier as s where s.supplier_status_id = 1",nativeQuery = true)
	public int getactiveSupplierCount();
}
