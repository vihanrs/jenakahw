package com.jenakahw.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Stock;
import com.jenakahw.repository.StockRepository;
import com.jenakahw.repository.StockStatusRepository;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.StockService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class StockServiceImpl implements StockService {
	// Make it final for immutability
	private final StockRepository stockRepository;
	private final StockStatusRepository stockStatusRepository;
	private final PrivilegeHelper privilegeHelper;
	private static final String MODULE = "Stock";

	// Constructor injection
	public StockServiceImpl(StockRepository stockRepository, StockStatusRepository stockStatusRepository,
			PrivilegeHelper privilegeHelper, AuthService authService) {
		this.stockRepository = stockRepository;
		this.stockStatusRepository = stockStatusRepository;
		this.privilegeHelper = privilegeHelper;
	}

	@Override
	public List<Stock> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return stockRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	@Override
	public List<Stock> getProductsByNameOrBarcode(String nameBarcode) {
		// check privileges
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return stockRepository.getStockProductListByNameBarcode(nameBarcode);
		} else {
			return null;
		}
	}

	@Override
	public String updateAllStockStatuses() {
		List<Stock> stockList = stockRepository.findAll(Sort.by(Direction.DESC, "id"));

		for (Stock stk : stockList) {
			updateStockStatus(stk.getId());
		}

		return "All Stock Status Updated";
	}

	@Override
	public String updateStockStatus(int stockId) {
		try {
			// get stock
			Stock stock = stockRepository.getReferenceById(stockId);
			if (stock.getAvailableQty().compareTo(BigDecimal.ZERO) == 0) {
				// update status to 'Out of Stock'
				stock.setStockStatus(stockStatusRepository.getReferenceById(2));
				stockRepository.save(stock);
				return "Out of Stock";
			} else {
				// get rol
				BigDecimal rol = BigDecimal.ZERO;

				if (stock.getProductId().getRol() != null) {
					rol = new BigDecimal(stock.getProductId().getRol());
				}

				// compare available stock with rol
				int compareStockQty = stock.getAvailableQty().compareTo(rol);
				if (compareStockQty <= 0) {
					// update status to 'Low Stock'
					stock.setStockStatus(stockStatusRepository.getReferenceById(3));
					stockRepository.save(stock);
					return "Low Stock";
				} else {
					// update status to 'In Stock'
					stock.setStockStatus(stockStatusRepository.getReferenceById(1));
					stockRepository.save(stock);
					return "In Stock";
				}
			}

		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
