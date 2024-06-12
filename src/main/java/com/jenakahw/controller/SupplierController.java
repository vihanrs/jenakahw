package com.jenakahw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Supplier;
import com.jenakahw.repository.SupplierRepository;
import com.jenakahw.repository.SupplierStatusRepository;

@RestController
@RequestMapping(value = "/supplier") // class level mapping
public class SupplierController {

	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private SupplierRepository supplierRepository;

	@Autowired
	private PrivilegeController privilegeController;
	
	@Autowired
	private SupplierStatusRepository supplierStatusRepository;

	// supplier UI service [/supplier -- return supplier UI]
	@GetMapping
	public ModelAndView supplierUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Supplier | Jenaka Hardware");
		modelAndView.addObject("logusername", auth.getName());
		modelAndView.setViewName("supplier.html");
		return modelAndView;
	}

	// get mapping for get all supplier data -- [/supplier/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Supplier> findAll() {
		if (privilegeController.hasPrivilege("Supplier", "select")) {
			return supplierRepository.findAll();
		} else {
			return null;
		}
	}

	// post mapping for save new supplier
	@PostMapping
	public String saveSupplier(@RequestBody Supplier supplier) {
		// check authentication authorization
		if (!privilegeController.hasPrivilege("Supplier", "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		// check contact
		Supplier extSupplierByContact = supplierRepository.getSupplierByContact(supplier.getContact());
		if (extSupplierByContact != null) {
			return "Supplier Save Not Completed : Contact No. " + supplier.getContact() + " is already exist!";
		}

		// check email
		Supplier extSupplierByEmail = supplierRepository.getSupplierByEmail(supplier.getEmail());
		if (extSupplierByEmail != null) {
			return "Supplier Save Not Completed : Email " + supplier.getEmail() + " is already exist!";
		}

		try {
			// set added date time
			supplier.setAddedDateTime(LocalDateTime.now());

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return "Supplier Save Not Completed : " + e.getMessage();
		}
	}

	// put mapping for update supplier
	@PutMapping
	public String updateSupplier(@RequestBody Supplier supplier) {
		// check authentication authorization
		if (!privilegeController.hasPrivilege("Supplier", "update")) {
			return "Access Denied !!!";
		}

		// check existing
		Supplier extSupplier = supplierRepository.getReferenceById(supplier.getId());
		if (extSupplier == null) {
			return "Update not completed : Supplier not available";
		}

		// check duplicates...
		// check contact
		Supplier extSupplierByContact = supplierRepository.getSupplierByContact(supplier.getContact());
		if (extSupplierByContact != null && supplier.getId() != extSupplierByContact.getId()) {
			return "Supplier Update Not Completed : Contact No. " + supplier.getContact() + " is already exist!";
		}

		// check email
		Supplier extSupplierByEmail = supplierRepository.getSupplierByEmail(supplier.getEmail());
		if (extSupplierByEmail != null && supplier.getId() != extSupplierByContact.getId()) {
			return "Supplier Update Not Completed : Email " + supplier.getEmail() + " is already exist!";
		}

		try {
			// set last updated date time
			supplier.setLastUpdatedDateTime(LocalDateTime.now());

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return "Supplier Update Not Completed : " + e.getMessage();
		}
	}

	// delete mapping for delete supplier
	@DeleteMapping
	public String deleteSupplier(@RequestBody Supplier supplier) {
		// check authentication authorization
		if (!privilegeController.hasPrivilege("Supplier", "delete")) {
			return "Access Denied !!!";
		}

		// check existing
		Supplier extSupplier = supplierRepository.getReferenceById(supplier.getId());
		if (extSupplier == null) {
			return "Delete not completed : Supplier Not Exist..!";
		}
		
		try {
			
			// set supplier statuts to 'Deleted'
			supplier.setSupplierStatusId(supplierStatusRepository.getReferenceById(2)); // set supplier status to 'Deleted'
			
			supplierRepository.save(supplier);
			
			return "OK";
		} catch (Exception e) {
			return "Supplier Delete Not Completed : " + e.getMessage();
		}

	}

}
