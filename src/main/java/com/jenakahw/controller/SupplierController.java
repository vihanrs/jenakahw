package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Supplier;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.SupplierService;

@RestController
@RequestMapping(value = "/supplier") // class level mapping
public class SupplierController {

	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private SupplierService supplierService ;

	@Autowired
	private AuthService authService;
	
	// supplier UI service [/supplier -- return supplier UI]
	@GetMapping
	public ModelAndView supplierUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Supplier | Jenaka Hardware");
		modelAndView.addObject("logusername", loggedUser.getUsername());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("supplier.html");
		return modelAndView;
	}

	// get mapping for get all supplier data -- [/supplier/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Supplier> findAll() {
		return supplierService.findAll();
	}
	
	// get mapping for get all active supplier selected data -- [/supplier/getactivesuppliers]
	@GetMapping(value = "/findactivesuppliers", produces = "application/json")
	public List<Supplier> findActiveSuppliers() {
		return supplierService.findActiveSuppliers();
	}

	// post mapping for save new supplier
	@PostMapping
	public String saveSupplier(@RequestBody Supplier supplier) {
		return supplierService.saveSupplier(supplier);
	}

	// put mapping for update supplier
	@PutMapping
	public String updateSupplier(@RequestBody Supplier supplier) {
		return supplierService.updateSupplier(supplier);
	}

	// delete mapping for delete supplier
	@DeleteMapping
	public String deleteSupplier(@RequestBody Supplier supplier) {
		return supplierService.deleteSupplier(supplier);
	}

}
