package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.Product;
import com.jenakahw.domain.PurchaseOrder;

public interface PurchaseOrderService {
	
	List<PurchaseOrder> findAll();

    List<Product> findPOProductsByPOID(Integer poId);

    POHasProduct findByPOIDAndProductId(Integer poId, Integer productId);

    List<PurchaseOrder> findPOByStatus(Integer poStatusId);

    List<PurchaseOrder> findPurchaseOrdersBySupplier(Integer supplierId);

    String savePurchaseOrder(PurchaseOrder purchaseOrder);

    String updatePurchaseOrder(PurchaseOrder purchaseOrder);

    String deletePurchaseOrder(PurchaseOrder purchaseOrder);
}
