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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Supplier;
import com.jenakahw.domain.SupplierBankDetails;
import com.jenakahw.repository.SupplierRepository;
import com.jenakahw.repository.SupplierStatusRepository;

import jakarta.transaction.Transactional;

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
	
	@Autowired
	private UserController userController;

	private static final String MODULE = "Supplier";
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
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return supplierRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}
	
	// get mapping for get all active supplier selected data -- [/supplier/getactivesuppliers]
	@GetMapping(value = "/findactivesuppliers", produces = "application/json")
	public List<Supplier> findActiveSuppliers() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return supplierRepository.findActiveSuppliers();
		} else {
			return null;
		}
	}

	// post mapping for save new supplier
	@Transactional
	@PostMapping
	public String saveSupplier(@RequestBody Supplier supplier) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		// check contact
		Supplier extSupplierByContact = supplierRepository.getSupplierByContact(supplier.getContact());
		if (extSupplierByContact != null) {
			return "Contact No. " + supplier.getContact() + " is already exist!";
		}

		// check email
		Supplier extSupplierByEmail = supplierRepository.getSupplierByEmail(supplier.getEmail());
		if (extSupplierByEmail != null) {
			return "Email " + supplier.getEmail() + " is already exist!";
		}

		try {
			// set added user
			supplier.setUserId(userController.getLoggedUser());
			// set added date time
			supplier.setAddedDateTime(LocalDateTime.now());
			
			for(SupplierBankDetails bankDetails : supplier.getBankDetails()) {
				bankDetails.setSupplierId(supplier);
			}

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// put mapping for update supplier
	@Transactional
	@PutMapping
	public String updateSupplier(@RequestBody Supplier supplier) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check existing
		Supplier extSupplier = supplierRepository.getReferenceById(supplier.getId());
		if (extSupplier == null) {
			return "Supplier not available";
		}

		// check duplicates...
		// check contact
		Supplier extSupplierByContact = supplierRepository.getSupplierByContact(supplier.getContact());
		if (extSupplierByContact != null && supplier.getId() != extSupplierByContact.getId()) {
			return "Contact No. " + supplier.getContact() + " is already exist!";
		}

		// check email
		Supplier extSupplierByEmail = supplierRepository.getSupplierByEmail(supplier.getEmail());
		if (extSupplierByEmail != null && supplier.getId() != extSupplierByEmail.getId()) {
			return "Email " + supplier.getEmail() + " is already exist!";
		}

		try {
			// set last updated date time
			supplier.setLastUpdatedDateTime(LocalDateTime.now());
			
			// set last updated user id
			supplier.setUpdatedUserId(userController.getLoggedUser().getId());
			
			for(SupplierBankDetails bankDetails : supplier.getBankDetails()) {
				bankDetails.setSupplierId(supplier);
			}

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete supplier
	@Transactional
	@DeleteMapping
	public String deleteSupplier(@RequestBody Supplier supplier) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check existing
		Supplier extSupplier = supplierRepository.getReferenceById(supplier.getId());
		if (extSupplier == null) {
			return "Supplier Not Exist..!";
		}
		
		try {
			//set deleted data and time
			supplier.setDeletedDateTime(LocalDateTime.now());

			//set deleted user id
			supplier.setDeletedUserId(userController.getLoggedUser().getId());
			
			// set supplier statuts to 'Deleted'
			supplier.setSupplierStatusId(supplierStatusRepository.getReferenceById(3)); 
			
			for(SupplierBankDetails bankDetails : supplier.getBankDetails()) {
				bankDetails.setSupplierId(supplier);
			}
			
			supplierRepository.save(supplier);
			
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}

	}

}
