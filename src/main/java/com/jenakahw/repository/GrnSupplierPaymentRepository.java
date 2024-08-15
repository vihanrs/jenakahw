package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.GrnHasSupplierPayment;

@Repository
public interface GrnSupplierPaymentRepository extends JpaRepository<GrnHasSupplierPayment, Integer>{

	// query for get grn supplier payments by supplier payment
	@Query("Select gsp from GrnHasSupplierPayment gsp where supplierPaymentId.id=?1")
	public List<GrnHasSupplierPayment> findGrnPaymentsSupplierPayment(int supplierPaymentId);
}
