package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	// query for get available products with status =
	// 'available' and barcode or name like given value
	@Query(value = "select p from Product p where p.name like ?1% or p.barcode like ?1 and p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') order by p.id desc")
	public List<Product> getProductListByNameBarcode(String nameBarcode);

	// query for get available products with selected columns and status =
	// 'available' and other filters with or without and without in selected
	// supplier
	@Query(value = "select new Product(p.id,p.name,p.barcode) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') "
			+ "and (:brandname is null or p.brandId.id = (select b.id from Brand b where b.name = :brandname))"
			+ "and (:categoryname is null or p.subCategoryId.categoryId.id = (select c.id from Category c where c.name = :categoryname))"
			+ "and (:subcategoryname is null or p.subCategoryId.id=(select sub.id from SubCategory sub where sub.name = :subcategoryname))"
			+ "and (:supplierid is null or p.id not in (select phs.productId.id from ProductHasSupplier phs where phs.supplierId.id= :supplierid)) order by p.id desc")
	public List<Product> getAvailableProducts(@Param("supplierid") Integer supplierId,
			@Param("brandname") String brandName, @Param("categoryname") String categoryName,
			@Param("subcategoryname") String subCategoryName);

	// query for get available products with selected columns and status =
	// 'available'
	// without in selected supplier
	@Query(value = "select new Product(p.id,p.name,p.barcode) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') "
			+ "and p.id not in (select phs.productId.id from ProductHasSupplier phs where phs.supplierId.id = ?1) order by p.id desc")
	public List<Product> getAvailableProductsWithoutSupplier(Integer supplierId);

	// query for get available products with selected columns and status =
	// 'available'
	// with in selected supplier
	@Query(value = "select new Product(p.id,p.name,p.barcode,p.unitTypeId) from Product p where p.productStatusId.id = (select ps.id from ProductStatus ps where ps.name = 'Available') "
			+ "and p.id in (select phs.productId.id from ProductHasSupplier phs where phs.supplierId.id = ?1) order by p.id desc")
	public List<Product> getAvailableProductsWithSupplier(Integer supplierId);

	// query for get product by name
	@Query(value = "Select p from Product p where p.name = ?1")
	public Product getProductByName(String name);

	// query for generate next barcode
	@Query(value = "SELECT concat(substring(YEAR(CURRENT_DATE()),3),lpad(substring(max(p.barcode),3)+1,6,0)) FROM jenakahw.product as p where year(p.added_datetime) = YEAR(CURRENT_DATE())", nativeQuery = true)
	public String getNextBarcode();
}
