package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	// query for get available products with selected columns and status =
	// 'available'
	@Query(value = "select new Product(p.id,p.name,p.barcode) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') order by p.id desc")
	public List<Product> getAvailableProducts();

	// query for get available products with selected columns and status =
	// 'available'
	// without in selected supplier
	@Query(value = "select new Product(p.id,p.name,p.barcode) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') "
			+ "and p.id not in (select phs.productId.id from ProductHasSupplier phs where phs.supplierId.id = ?1) order by p.id desc")
	public List<Product> getAvailableProductsWithoutSupplier(Integer supplierId);

	// query for get available products with selected columns and status = 'available'
	// with in selected supplier
	@Query(value = "select new Product(p.id,p.name,p.barcode) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') "
			+ "and p.id in (select phs.productId.id from ProductHasSupplier phs where phs.supplierId.id = ?1) order by p.id desc")
	public List<Product> getAvailableProductsWithSupplier(Integer supplierId);
}
