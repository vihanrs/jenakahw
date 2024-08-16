package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Stock;
import com.jenakahw.domain.User;
import com.jenakahw.repository.StockRepository;

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
	private UserController userController;

	@Autowired
	private PrivilegeController privilegeController;

	private static final String MODULE = "Stock";

	// Stock UI service [/stock -- return Stock UI]
	@GetMapping
	public ModelAndView grnUI() {
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
}
