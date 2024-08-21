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
	
	// query for get completed invoice count since last month
	@Query(value = "SELECT count(inv.id) FROM jenakahw.invoice as inv where inv.invoice_status_id = (select invs.id from jenakahw.invoice_status as invs where invs.name='Completed') and month(inv.added_datetime) = month(current_date())",nativeQuery = true)
	public int getCompletedInvoiceCountSinceLastMonth();

	// query for get today pending invoices today
	@Query(value = "SELECT count(inv.id) FROM jenakahw.invoice as inv where inv.invoice_status_id = (select invs.id from jenakahw.invoice_status as invs where invs.name='Pending') and date(inv.added_datetime) = current_date()",nativeQuery = true)
	public int getPendingInvoicesToday();
	
	// query for get invoices total since last month
	@Query(value = "select * from jenakahw.invoice as inv where year(inv.added_datetime) = year(current_date()) and month(inv.added_datetime) = month(current_date()) and inv.invoice_status_id = (select invs.id from jenakahw.invoice_status as invs where invs.name = 'Completed')",nativeQuery = true)
	public List<Invoice> getInvoicesSinceLastMonth();
	
	// query for get top five selling products in last 3 months
	@Query(value = "SELECT p.name,b.name as brand,c.name as category, sc.name as subcategory,ihp.product_id,sum(ihp.qty) as sellqty,sum(ihp.line_amount) as total_amount "
			+ "FROM jenakahw.invoice_has_product as ihp JOIN jenakahw.invoice as i ON ihp.invoice_id = i.id JOIN jenakahw.product as p ON ihp.product_id = p.id "
			+ "JOIN jenakahw.brand as b ON p.brand_id = b.id JOIN jenakahw.subcategory as sc ON p.subcategory_id = sc.id "
			+ "JOIN jenakahw.category as c ON sc.category_id=c.id WHERE i.added_datetime >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) "
			+ "group by ihp.product_id order by sellqty desc limit 5;",nativeQuery = true)
	public String[][] getTopSellingProducts();
	
	// sales report queries 
	
	// query for get daily income expenses report
	@Query(value = "SELECT DAYNAME(DATE(invp.added_datetime)) as day_of_week, DATE(invp.added_datetime) as date, SUM(invp.paid_amount) as amount, 'Invoice' as type FROM jenakahw.invoice_has_payment invp WHERE DATE(invp.added_datetime) "
			+ "BETWEEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AND CURDATE() GROUP BY DAYNAME(DATE(invp.added_datetime)), DATE(invp.added_datetime) UNION ALL "
			+ "SELECT DAYNAME(DATE(dein.added_datetime)) as day_of_week, DATE(dein.added_datetime) as date, SUM(dein.total) as amount, 'Extra Income' as type FROM daily_extra_income dein "
			+ "WHERE DATE(dein.added_datetime) BETWEEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AND CURDATE() AND dein.daily_income_expenses_status_id = 1 GROUP BY DAYNAME(DATE(dein.added_datetime)), DATE(dein.added_datetime) UNION ALL "
			+ "SELECT DAYNAME(DATE(sp.added_datetime)) as day_of_week, DATE(sp.added_datetime) as date, SUM(sp.paid_amount) as amount, 'Supplier Payment' as type FROM supplier_payment sp "
			+ "WHERE DATE(sp.added_datetime) BETWEEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AND CURDATE() GROUP BY DAYNAME(DATE(sp.added_datetime)), DATE(sp.added_datetime) UNION ALL "
			+ "SELECT DAYNAME(DATE(dexp.added_datetime)) as day_of_week, DATE(dexp.added_datetime) as date, SUM(dexp.total) as amount, 'Expense' as type FROM daily_expenses dexp "
			+ "WHERE DATE(dexp.added_datetime) BETWEEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AND CURDATE() AND dexp.daily_income_expenses_status_id = 1 GROUP BY DAYNAME(DATE(dexp.added_datetime)), DATE(dexp.added_datetime)",nativeQuery = true)
	public String[][] getDailyFinancialSummary();
	
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
