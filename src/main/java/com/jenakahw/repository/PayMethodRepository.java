package com.jenakahw.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.PayMethod;

@Repository
public interface PayMethodRepository extends JpaRepository<PayMethod, Integer> {

}
