package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.GrnHasSupplierPayment;
import com.jenakahw.domain.SupplierPayment;

public interface SupplierPaymentService {
	
	List<SupplierPayment> findAll();
	
    List<SupplierPayment> findAllByUser(int userId);
    
    List<GrnHasSupplierPayment> findGrnPaymentsBySupplierPayment(int supplierPaymentId);
    
    String saveSupplierPayment(SupplierPayment supplierPayment);
}
