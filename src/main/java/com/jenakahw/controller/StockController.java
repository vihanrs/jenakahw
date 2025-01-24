package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Stock;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.StockService;

@RestController
@RequestMapping(value = "/stock") // class level mapping
public class StockController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private StockService stockService;

	@Autowired
	private AuthService authService;

	// Stock UI service [/stock -- return Stock UI]
	@GetMapping
	public ModelAndView stockUI() {
		SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Stock | Jenaka Hardware");
		modelAndView.addObject("logusername", loggedUser.getUsername());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("stock.html");
		return modelAndView;
	}

	// get mapping for get all stock data -- [/stock/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Stock> findAll() {
		return stockService.findAll();
	}

	// get mapping for get available products by name or barcode
	@GetMapping(value = "/findstocksbyproductnamebarcode/{namebarcode}", produces = "application/json")
	public List<Stock> getProductsByNameOrBarcode(@PathVariable("namebarcode") String nameBarcode) {
		return stockService.getProductsByNameOrBarcode(nameBarcode);
	}
	
	// method to update all stock status
	@GetMapping(value = "/updatestockstatus")
	public String updateStatus() {
		return stockService.updateAllStockStatuses();
	}
	
	// method to update selected stock status
	@PutMapping(value = "/updatestockstatus/{stockId}")
    public String updateStockStatus(@PathVariable("stockId") int stockId) {
        return stockService.updateStockStatus(stockId);
    }
}
