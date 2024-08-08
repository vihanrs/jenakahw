package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.Invoice;
import com.jenakahw.domain.PurchaseOrder;

@Repository
public interface ReportRepository extends JpaRepository<PurchaseOrder, Integer> {

	// Dashboard Reports

	// query for get ACTIVE supplier count
	@Query(value = "Select count(s.id) from Supplier as s where s.supplier_status_id = 1", nativeQuery = true)
	public int getactiveSupplierCount1();

	// query for get customer list since last month
	@Query(value = "SELECT count(c.id) as count FROM jenakahw.customer as c where month(c.added_datetime) = month(current_date())", nativeQuery = true)
	public int getCustomerCountSinceLastMonth();
	
	// query for get pending purchase order count
	@Query(value = "Select count(po.id) from jenakahw.purchase_order as po where po.purchase_order_status_id = (select pos.id from jenakahw.purchase_order_status as pos where pos.name='Requested')",nativeQuery = true)
	public int getPendingPOCount();
	
	// query for get completed invoice count
	@Query(value = "SELECT count(inv.id) FROM jenakahw.invoice as inv where inv.invoice_status_id = (select invs.id from jenakahw.invoice_status as invs where invs.name='Completed') and month(inv.added_datetime) = month(current_date())",nativeQuery = true)
	public int getCompletedInvoiceCountSinceLastMonth();

	// query for get today pending invoices today
	@Query(value = "SELECT count(inv.id) FROM jenakahw.invoice as inv where inv.invoice_status_id = (select invs.id from jenakahw.invoice_status as invs where invs.name='Pending') and date(inv.added_datetime) = current_date()",nativeQuery = true)
	public int getPendingInvoicesToday();
	
	// query for get invoices total since last month
	@Query(value = "select * from jenakahw.invoice as inv where year(inv.added_datetime) = year(current_date()) and month(inv.added_datetime) = month(current_date()) and inv.invoice_status_id = (select invs.id from jenakahw.invoice_status as invs where invs.name = 'Completed')",nativeQuery = true)
	public List<Invoice> getInvoicesSinceLastMonth();
	
	// purchase order reports queries

	// query for get purchase order by status
	@Query(value = "Select p from PurchaseOrder p where p.purchaseOrderStatusId.id = ?1 ")
	public List<PurchaseOrder> purchaseOrderByStatus(int statusId);

	// query for get purchase order by supplier
	@Query(value = "Select p from PurchaseOrder p where p.supplierId.id = ?1 ")
	public List<PurchaseOrder> purchaseOrderBySupplier(int supplierId);

	// query for get purchaseorder by status and supplier
	@Query(value = "Select p from PurchaseOrder p where p.purchaseOrderStatusId.id = ?1 and supplierId.id=?2")
	public List<PurchaseOrder> purchaseOrderByStatusAndSupplier(int statusId, int supplierId);

	// query for get purchase order summary by supplier
	@Query(value = "select s.first_name,s.company,count(po.id),sum(po.total_amount) from purchase_order as po, supplier as s where po.supplier_id = s.id and po.purchase_order_status_id = 1 group by po.supplier_id", nativeQuery = true)
	public String[][] purchaseOrderSummaryBysupplier();

	// query for get ACTIVE supplier count
	@Query(value = "Select count(s.id) from Supplier as s where s.supplier_status_id = 1", nativeQuery = true)
	public int getactiveSupplierCount();

	// GRN reports queries

	// query for get grns by supplier
	@Query(value = "Select g from Grn g where g.supplierId =?1")
	public List<Grn> grnBySupplierId(int supplierId);

	// query for get grn summary monthly
	@Query(value = "Select month(g.added_datetime),sum(g.grand_total) from Grn as g where g.grn_status_id=1 and year(g.added_datetime) = year(CURRENT_DATE()) group by month(g.added_datetime)", nativeQuery = true)
	public String[][] grnSummaryByMonthly();

}
