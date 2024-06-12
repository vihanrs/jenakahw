package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

}
