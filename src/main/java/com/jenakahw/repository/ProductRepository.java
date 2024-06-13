package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	//query for get available products with selected columns and available status
	@Query(value = "select new Product(p.id,p.name,p.barcode) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') order by p.id desc")
	public List<Product> getAvailableProducts();
}
