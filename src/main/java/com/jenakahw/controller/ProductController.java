package com.jenakahw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Product;
import com.jenakahw.repository.ProductRepository;
import com.jenakahw.repository.ProductStatusRepository;

@RestController
// add class level mapping /product
@RequestMapping(value = "/product")
public class ProductController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private ProductStatusRepository productStatusRepository;

	@Autowired
	private UserController userController;

	private static final String MODULE = "Product";

	// get mapping for generate product UI
	@GetMapping
	public ModelAndView getProductUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView productView = new ModelAndView();
		productView.addObject("title", "Product  | Jenaka Hardware");
		productView.addObject("logusername", auth.getName());
		productView.setViewName("product.html");
		return productView;
	}

	// get service mapping for get all products
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Product> findAll() {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return productRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}

	// get mapping for get available product list
	@GetMapping(value = "/availablelist", produces = "application/json")
	public List<Product> getAvilableProducts(@RequestParam(value = "supplierid",required = false)Integer supplierId,@RequestParam(value = "brandname", required = false) String brandName,@RequestParam(value = "categoryname",required = false) String categoryName,@RequestParam(value = "subcategoryname",required = false)String subCategoryName) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return productRepository.getAvailableProducts(supplierId,brandName,categoryName,subCategoryName);
		} else {
			return null;
		}
	}

	// get mapping for get available product list without selected supplier
	@GetMapping(value = "/availablelistWithoutSupplier/{supplierId}", produces = "application/json")
	public List<Product> getAvilableProductsWithoutSupplier(@PathVariable("supplierId") Integer supplierId) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return productRepository.getAvailableProductsWithoutSupplier(supplierId);
		} else {
			return null;
		}
	}

	// get mapping for get available product list with selected supplier
	@GetMapping(value = "/availablelistWithSupplier/{supplierId}", produces = "application/json")
	public List<Product> getAvilableProductsWithSupplier(@PathVariable("supplierId") Integer supplierId) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return productRepository.getAvailableProductsWithSupplier(supplierId);
		} else {
			return null;
		}
	}

	// post mapping for save new product
	@PostMapping
	public String savePrivilege(@RequestBody Product product) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
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
			product.setAddedUserId(userController.getLoggedUser().getId());

			productRepository.save(product);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// put mapping for update product
	@PutMapping
	public String updateUser(@RequestBody Product product) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
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
			product.setUpdatedUserId(userController.getLoggedUser().getId());

			productRepository.save(product);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete product [/user]
	@DeleteMapping
	public String deleteUser(@RequestBody Product product) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
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
			product.setDeletedUserId(userController.getLoggedUser().getId());

			// set product statuts to 'Deleted'
			product.setProductStatusId(productStatusRepository.getReferenceById(3));

			productRepository.save(product);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
