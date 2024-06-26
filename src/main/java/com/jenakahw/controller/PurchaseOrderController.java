package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.repository.PurchaseOrderRepository;

@RestController
@RequestMapping(value = "/purchaseorder") // class level mapping
public class PurchaseOrderController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	private PrivilegeController privilegeController;

	// supplier UI service [/purchaseorder -- return Purchase Order UI]
	@GetMapping
	public ModelAndView purchaseOrderUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Purchase Order | Jenaka Hardware");
		modelAndView.addObject("logusername", auth.getName());
		modelAndView.setViewName("purchaseorder.html");
		return modelAndView;
	}
	
	// get mapping for get all purchaseorder data -- [/purchaseorder/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<PurchaseOrder> findAll() {
		if (privilegeController.hasPrivilege("Supplier", "select")) {
			return purchaseOrderRepository.findAll(Sort.by(Direction.DESC, "id"));
		} else {
			return null;
		}
	}
}
