package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.SupplierPayment;

@Repository
public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, Integer> {
	// query for generate next invoice code formate - 'INVyymmdd0001'
	@Query(value = "SELECT concat('INVS',substring(YEAR(CURRENT_DATE()),3),lpad(MONTH(CURRENT_DATE()),2,0),lpad(DAY(CURRENT_DATE()),2,0),lpad(substring(max(inv.payment_invoice_id),11)+1,3,0)) as next_invoice_id "
			+ "FROM jenakahw.supplier_payment as inv where DATE(inv.added_datetime) = CURRENT_DATE();", nativeQuery = true)
	public String getNextPayInvoiceID();
	
	// query for get all supplier payments by added user
	@Query(value = "Select sp from SupplierPayment sp where addedUserId = ?1 order by sp.id")
	public List<SupplierPayment> findAllByUser(int userId);
}
