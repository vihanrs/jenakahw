package com.jenakahw.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Stock;
import com.jenakahw.domain.User;
import com.jenakahw.repository.StockRepository;
import com.jenakahw.repository.StockStatusRepository;

@RestController
@RequestMapping(value = "/stock") // class level mapping
public class StockController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockStatusRepository stockStatusRepository;

	@Autowired
	private UserController userController;

	@Autowired
	private PrivilegeController privilegeController;

	private static final String MODULE = "Stock";

	// Stock UI service [/stock -- return Stock UI]
	@GetMapping
	public ModelAndView stockUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Stock | Jenaka Hardware");
		modelAndView.addObject("logusername", auth.getName());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("stock.html");
		return modelAndView;
	}

	// get mapping for get all stock data -- [/stock/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Stock> findAll() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return stockRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}

	// get mapping for get available products by name or barcode
	@GetMapping(value = "/findstocksbyproductnamebarcode/{namebarcode}", produces = "application/json")
	public List<Stock> getProductsByNameOrBarcode(@PathVariable("namebarcode") String nameBarcode) {
		// check privileges
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return stockRepository.getStockProductListByNameBarcode(nameBarcode);
		} else {
			return null;
		}
	}
	
	// method to update all stock status
	@GetMapping(value = "/updatestockstatus")
	public String updateStatus() {
		List<Stock> stockList = stockRepository.findAll(Sort.by(Direction.DESC, "id"));
		
		for(Stock stk : stockList) {
			updateStockStatus(stk.getId());
		}
		
		return "All Stock Status Updated";
	}

	// method to manage stock status
	public String updateStockStatus(int stockId) {
		
		try {
			// get stock
			Stock stock = stockRepository.getReferenceById(stockId);
			if (stock.getAvailableQty().compareTo(BigDecimal.ZERO) == 0) {
				System.err.println(stock.getProductId().getName());
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
