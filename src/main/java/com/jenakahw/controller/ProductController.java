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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;
import com.jenakahw.domain.Product;
import com.jenakahw.repository.ProductRepository;

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
	private UserController userController;

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
		if (privilegeController.hasPrivilege("Product", "select")) {
			return productRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}

	}

	// get mapping for get available product list
	@GetMapping(value = "/availablelist", produces = "application/json")
	public List<Product> getAvilableProducts() {
		// check privileges
		if (privilegeController.hasPrivilege("Product", "select")) {
			return productRepository.getAvailableProducts();
		} else {
			return null;
		}
	}

	// get mapping for get available product list without selected supplier
	@GetMapping(value = "/availablelistWithoutSupplier/{supplierId}", produces = "application/json")
	public List<Product> getAvilableProductsWithoutSupplier(@PathVariable("supplierId") Integer supplierId) {
		// check privileges
		if (privilegeController.hasPrivilege("Product", "select")) {
			return productRepository.getAvailableProductsWithoutSupplier(supplierId);
		} else {
			return null;
		}
	}

	// get mapping for get available product list with selected supplier
	@GetMapping(value = "/availablelistWithSupplier/{supplierId}", produces = "application/json")
	public List<Product> getAvilableProductsWithSupplier(@PathVariable("supplierId") Integer supplierId) {
		// check privileges
		if (privilegeController.hasPrivilege("Product", "select")) {
			return productRepository.getAvailableProductsWithSupplier(supplierId);
		} else {
			return null;
		}
	}

	// post mapping for save new product
	@PostMapping
	public String savePrivilege(@RequestBody Product product) {
		// check privileges
		if (!privilegeController.hasPrivilege("Product", "insert")) {
			return "Access Denied !!!";
		}
		// get logged user
		User loggedUser = userController.getLoggedUser();

		// check privileges
		if (!privilegeController.hasPrivilege("Privilege", "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...

		try {
			// set added date time
			product.setAddedDateTime(LocalDateTime.now());
			// set added user
			product.setUserId(loggedUser);

			productRepository.save(product);
			return "OK";
		} catch (Exception e) {
			return "Save not completed : " + e.getMessage();
		}
	}

	// put mapping for update product
	@PutMapping
	public String updateUser(@RequestBody Product product) {
		// check privileges
		if (!privilegeController.hasPrivilege("Product", "update")) {
			return "Access Denied !!!";
		}
		// get logged user
		User loggedUser = userController.getLoggedUser();

		// check duplicates...

		try {
			// set last updated date time
			product.setLastUpdatedDateTime(LocalDateTime.now());
			// set updated user id
			product.setUpdatedUserId(loggedUser.getId());

			productRepository.save(product);
			return "OK";
		} catch (Exception e) {
			return "Update not completed : " + e.getMessage();
		}
	}

	// delete mapping for delete user account [/user]
	@DeleteMapping
	public String deleteUser(@RequestBody Product product) {
		// check privileges
		if (!privilegeController.hasPrivilege("Product", "delete")) {
			return "Access Denied !!!";
		}

		// check given product exist or not

		try {
			return "OK";
		} catch (Exception e) {
			return "Delete not complete :" + e.getMessage();
		}
	}

}
