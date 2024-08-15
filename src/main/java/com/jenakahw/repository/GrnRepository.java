package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Grn;

@Repository
public interface GrnRepository extends JpaRepository<Grn, Integer> {

	// query for generate next grn code formate - 'GRNyyyymm001'
	@Query(value = "SELECT concat('GRN',YEAR(CURRENT_DATE()),lpad(MONTH(CURRENT_DATE()),2,0),lpad(substring(max(grn.grn_code),10)+1,3,0)) as next_grn_code "
			+ "FROM jenakahw.grn as grn where MONTH(DATE(grn.added_datetime)) = MONTH(CURRENT_DATE())", nativeQuery = true)
	public String getNextGRNCode();
	
	// query for get all grns
	public List<Grn> findAll();
	
	// query for get grn by id
	@Query(value = "Select g from Grn g where g.id=?1")
	public Grn getGrnById(Integer gid);
	
	// query for get all incomplete grns by supplier
	@Query(value = "Select new Grn(g.id,g.grnCode,g.supplierId,g.grandTotal,g.paid,g.balanceAmount,g.grnStatusId) from Grn g where g.grandTotal != g.paid and g.supplierId = ?1 order by g.id")
	public List<Grn> findAllIncompleteBySupplier(int supplierId);
	
}
