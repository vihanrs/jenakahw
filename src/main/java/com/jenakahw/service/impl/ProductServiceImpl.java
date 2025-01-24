package com.jenakahw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Product;
import com.jenakahw.repository.ProductRepository;
import com.jenakahw.repository.ProductStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.ProductService;
import com.jenakahw.util.PrivilegeHelper;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	// Make it final for immutability
	private final ProductRepository productRepository;
	private final ProductStatusRepository productStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Product";

	// Constructor injection
	public ProductServiceImpl(ProductRepository productRepository, ProductStatusRepository productStatusRepository,
			PrivilegeHelper privilegeHelper, AuthService authService) {
		this.productRepository = productRepository;
		this.productStatusRepository = productStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<Product> findAll() {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return productRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public List<Product> getProductsByNameOrBarcode(String nameBarcode) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return productRepository.getProductListByNameBarcode(nameBarcode);
		} else {
			return null;
		}
	}

	@Override
	public List<Product> getAvailableProducts(Integer supplierId, String brandName, String categoryName,
			String subCategoryName) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return productRepository.getAvailableProducts(supplierId, brandName, categoryName, subCategoryName);
		} else {
			return null;
		}
	}

	@Override
	public List<Product> getAvailableProductsWithoutSupplier(Integer supplierId) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return productRepository.getAvailableProductsWithoutSupplier(supplierId);
		} else {
			return null;
		}
	}

	@Override
	public List<Product> getAvailableProductsWithSupplier(Integer supplierId) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return productRepository.getAvailableProductsWithSupplier(supplierId);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public String saveProduct(Product product) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		Product extProductByName = productRepository.getProductByName(product.getName());
		if (extProductByName != null) {
			return "Product Name Already Exist...!";
		}

		try {
			// generate barcode
			product.setBarcode(productRepository.getNextBarcode());

			// set added date time
			product.setAddedDateTime(LocalDateTime.now());
			// set added user
			product.setAddedUserId(authService.getLoggedUser().getId());

			productRepository.save(product);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String updateProduct(Product product) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		Product extProductByName = productRepository.getProductByName(product.getName());
		if (extProductByName != null && product.getId() != extProductByName.getId()) {
			return "Product Name Already Exist...!";
		}

		try {
			// set last updated date time
			product.setLastUpdatedDateTime(LocalDateTime.now());
			// set updated user id
			product.setUpdatedUserId(authService.getLoggedUser().getId());

			productRepository.save(product);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String deleteProduct(Product product) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check given product exist or not
		Product extProduct = productRepository.getReferenceById(product.getId());
		if (extProduct == null) {
			return "Product Not Exist..!";
		}

		try {
			// set deleted data and time
			product.setDeletedDateTime(LocalDateTime.now());

			// set deleted user id
			product.setDeletedUserId(authService.getLoggedUser().getId());

			// set product statuts to 'Deleted'
			product.setProductStatusId(productStatusRepository.getReferenceById(3));

			productRepository.save(product);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
