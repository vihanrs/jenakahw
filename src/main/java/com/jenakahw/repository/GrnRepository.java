package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Grn;

@Repository
public interface GrnRepository extends JpaRepository<Grn, Integer> {

	// query for generate next grn code formate - 'GRNyyyymm001'
	@Query(value = "SELECT concat('GRN',YEAR(CURRENT_DATE()),lpad(MONTH(CURRENT_DATE()),2,0),lpad(substring(max(grn.grn_code),10)+1,3,0)) as next_grn_code "
			+ "FROM jenakahw.grn as grn where DATE(grn.added_datetime) = CURRENT_DATE()", nativeQuery = true)
	public String getNextGRNCode();
}
