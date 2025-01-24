package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Product;

public interface ProductService {
	
	List<Product> findAll();
	
    List<Product> getProductsByNameOrBarcode(String nameBarcode);
    
    List<Product> getAvailableProducts(Integer supplierId, String brandName, String categoryName, String subCategoryName);
    
    List<Product> getAvailableProductsWithoutSupplier(Integer supplierId);
    
    List<Product> getAvailableProductsWithSupplier(Integer supplierId);
    
    String saveProduct(Product product);
    
    String updateProduct(Product product);
    
    String deleteProduct(Product product);
}
