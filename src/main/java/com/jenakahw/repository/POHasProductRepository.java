package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.Product;

@Repository
public interface POHasProductRepository extends JpaRepository<POHasProduct, Integer>{
	
	@Query(value = "Select pohp.productId from POHasProduct pohp where purchaseOrderId.id =?1")
	public List<Product> findPOProductsByPOID(Integer purchaseOrderId);

	@Query(value = "Select new POHasProduct(pohp.id,pohp.purchasePrice,pohp.qty) from POHasProduct pohp where purchaseOrderId.id =?1 and productId.id =?2")
	public POHasProduct findByPOIDAndProductId(int poId,int productId);
	
}
