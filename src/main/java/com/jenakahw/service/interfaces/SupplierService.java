package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Supplier;

public interface SupplierService {
	List<Supplier> findAll();
    List<Supplier> findActiveSuppliers();
    String saveSupplier(Supplier supplier);
    String updateSupplier(Supplier supplier);
    String deleteSupplier(Supplier supplier);
}
