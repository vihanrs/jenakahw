package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.ProductService;

@RestController
// add class level mapping /product
@RequestMapping(value = "/product")
public class ProductController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private ProductService productService;

	@Autowired
	private AuthService authService;

	// get mapping for generate product UI
	@GetMapping
	public ModelAndView getProductUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView productView = new ModelAndView();
		productView.addObject("title", "Product  | Jenaka Hardware");
		productView.addObject("logusername", loggedUser.getUsername());
		productView.addObject("loguserrole", userRole);
		productView.addObject("loguserphoto", loggedUser.getUserPhoto());
		productView.setViewName("product.html");
		return productView;
	}

	// get service mapping for get all products
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Product> findAll() {
		return productService.findAll();
	}

	// get mapping for get available products by name or barcode
	@GetMapping(value = "/findproductlistbynamebarcode/{namebarcode}", produces = "application/json")
	public List<Product> getProductsByNameOrBarcode(@PathVariable("namebarcode") String nameBarcode) {
		return productService.getProductsByNameOrBarcode(nameBarcode);
	}

	// get mapping for get available product list
	@GetMapping(value = "/availablelist", produces = "application/json")
	public List<Product> getAvilableProducts(@RequestParam(value = "supplierid", required = false) Integer supplierId,
			@RequestParam(value = "brandname", required = false) String brandName,
			@RequestParam(value = "categoryname", required = false) String categoryName,
			@RequestParam(value = "subcategoryname", required = false) String subCategoryName) {
		return productService.getAvailableProducts(supplierId, brandName, categoryName, subCategoryName);
	}

	// get mapping for get available product list without selected supplier
	@GetMapping(value = "/availablelistWithoutSupplier/{supplierId}", produces = "application/json")
	public List<Product> getAvilableProductsWithoutSupplier(@PathVariable("supplierId") Integer supplierId) {
		return productService.getAvailableProductsWithoutSupplier(supplierId);
	}

	// get mapping for get available product list with selected supplier
	@GetMapping(value = "/availablelistWithSupplier/{supplierId}", produces = "application/json")
	public List<Product> getAvilableProductsWithSupplier(@PathVariable("supplierId") Integer supplierId) {
		return productService.getAvailableProductsWithSupplier(supplierId);
	}

	// post mapping for save new product
	@PostMapping
	public String saveProduct(@RequestBody Product product) {
		return productService.saveProduct(product);
	}

	// put mapping for update product
	@PutMapping
	public String updateProduct(@RequestBody Product product) {
		return productService.updateProduct(product);
	}

	// delete mapping for delete product
	@DeleteMapping
	public String deleteProduct(@RequestBody Product product) {
		return productService.deleteProduct(product);
	}

}
