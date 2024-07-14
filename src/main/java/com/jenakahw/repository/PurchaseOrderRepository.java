package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer>{

	
	// query for generate next purchase order code formate - 'PO240101001'
	@Query(value = "SELECT concat('PO',substring(YEAR(CURRENT_DATE()),3),lpad(MONTH(CURRENT_DATE()),2,0),lpad(DAY(CURRENT_DATE()),2,0),lpad(substring(max(po.po_code),9)+1,3,0)) as next_po_code "
			+ "FROM jenakahw.purchase_order as po where DATE(po.added_datetime) = CURRENT_DATE()",nativeQuery = true)
	public String getNextPOCode();
}
