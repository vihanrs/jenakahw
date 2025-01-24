package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.Stock;

public interface StockService {
	
	List<Stock> findAll();
	
    List<Stock> getProductsByNameOrBarcode(String nameBarcode);
    
    String updateAllStockStatuses();
    
    String updateStockStatus(int stockId);
}
