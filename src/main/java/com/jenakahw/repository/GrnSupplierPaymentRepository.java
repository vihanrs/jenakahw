package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.GrnHasSupplierPayment;

@Repository
public interface GrnSupplierPaymentRepository extends JpaRepository<GrnHasSupplierPayment, Integer>{

}
