package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	// query for generate next invoice code formate - 'INVyymmdd0001'
	@Query(value = "SELECT concat('INV',substring(YEAR(CURRENT_DATE()),3),lpad(MONTH(CURRENT_DATE()),2,0),lpad(DAY(CURRENT_DATE()),2,0),lpad(substring(max(inv.invoice_id),10)+1,3,0)) as next_invoice_id "
			+ "FROM jenakahw.invoice as inv where DATE(inv.added_datetime) = CURRENT_DATE();", nativeQuery = true)
	public String getNextInvoiceID();
	
	// query for find invoices by status
	@Query(value = "Select inv from Invoice inv where inv.invoiceStatusId = (Select invstatus from InvoiceStatus invstatus where invstatus.name =?1)")
	public List<Invoice> findByStatus(String status);
	
	// query for select invoice by id
	@Query(value = "Select inv from Invoice inv where inv.invoiceId = ?1")
	public Invoice findByInvoiceId(String invoiceId);

	// query for get invoice list by customer incomplete payments
	@Query(value = "Select inv from Invoice inv where inv.customerId.id=?1 and inv.balanceAmount != 0 order by inv.id")
	public List<Invoice> findByCustomerAndIncomplete(int customerId);
	
	// query for get all invoices in current date
	@Query(value = "Select inv from Invoice inv where DATE(inv.addedDateTime) between DATE(:fromDate) and DATE(:toDate)")
	public List<Invoice> findAllInDateRange(@Param("fromDate") String fromDate,@Param("toDate") String toDate);
}

