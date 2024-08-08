package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.CustomerHasPayment;
@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerHasPayment, Integer>{

}
