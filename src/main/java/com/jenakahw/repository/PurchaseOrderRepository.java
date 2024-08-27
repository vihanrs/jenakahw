package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer>{

	
	// query for generate next purchase order code formate - 'POyyyymm001'
	@Query(value = "SELECT concat('PO',YEAR(CURRENT_DATE()),lpad(MONTH(CURRENT_DATE()),2,0),lpad(substring(max(po.po_code),9)+1,3,0)) as next_po_code "
			+ "FROM jenakahw.purchase_order as po where MONTH(DATE(po.added_datetime)) = MONTH(CURRENT_DATE());",nativeQuery = true)
	public String getNextPOCode();
	
	// query for get purchase orders by status
	@Query(value = "Select new PurchaseOrder(po.id,po.poCode,po.supplierId) from PurchaseOrder po where po.purchaseOrderStatusId.id = ?1")
	public List<PurchaseOrder> findPurchaseOrdersByStatus(int poStatusId);
	
	
	@Query(value = "Select new PurchaseOrder(po.id,po.poCode,po.supplierId) from PurchaseOrder po where po.purchaseOrderStatusId.id = 1 and supplierId.id=?1")
	public List<PurchaseOrder> findPurchaseOrdersBySupplier(int supplierId);
	
}
