package com.jenakahw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Supplier;
import com.jenakahw.domain.SupplierBankDetails;
import com.jenakahw.repository.SupplierRepository;
import com.jenakahw.repository.SupplierStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.SupplierService;
import com.jenakahw.util.PrivilegeHelper;

import jakarta.transaction.Transactional;

@Service
public class SupplierServiceImpl implements SupplierService {
	// Make it final for immutability
	private final SupplierRepository supplierRepository;
	private final SupplierStatusRepository supplierStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private final AuthService authService;

	private static final String MODULE = "Supplier";

	// Constructor injection
	public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierStatusRepository supplierStatusRepository,
			PrivilegeHelper privilegeHelper, AuthService authService) {
		this.supplierRepository = supplierRepository;
		this.supplierStatusRepository = supplierStatusRepository;
		this.privilegeHelper = privilegeHelper;
		this.authService = authService;
	}

	@Override
	public List<Supplier> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return supplierRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public List<Supplier> findActiveSuppliers() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return supplierRepository.findActiveSuppliers();
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public String saveSupplier(Supplier supplier) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
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
			supplier.setUserId(authService.getLoggedUser());
			// set added date time
			supplier.setAddedDateTime(LocalDateTime.now());

			for (SupplierBankDetails bankDetails : supplier.getBankDetails()) {
				bankDetails.setSupplierId(supplier);
			}

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String updateSupplier(Supplier supplier) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
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
			supplier.setUpdatedUserId(authService.getLoggedUser().getId());

			for (SupplierBankDetails bankDetails : supplier.getBankDetails()) {
				bankDetails.setSupplierId(supplier);
			}

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	@Transactional
	public String deleteSupplier(Supplier supplier) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// check existing
		Supplier extSupplier = supplierRepository.getReferenceById(supplier.getId());
		if (extSupplier == null) {
			return "Supplier Not Exist..!";
		}

		try {
			// set deleted data and time
			supplier.setDeletedDateTime(LocalDateTime.now());

			// set deleted user id
			supplier.setDeletedUserId(authService.getLoggedUser().getId());

			// set supplier statuts to 'Deleted'
			supplier.setSupplierStatusId(supplierStatusRepository.getReferenceById(3));

			for (SupplierBankDetails bankDetails : supplier.getBankDetails()) {
				bankDetails.setSupplierId(supplier);
			}

			supplierRepository.save(supplier);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
