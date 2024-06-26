package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.PurchaseOrderStatus;

@Repository
public interface PurchaseOrderStatusRepository extends JpaRepository<PurchaseOrderStatus, Integer> {

}
