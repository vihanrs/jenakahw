package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.Product;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.PurchaseOrderService;

@RestController
@RequestMapping(value = "/purchaseorder") // class level mapping
public class PurchaseOrderController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private PurchaseOrderService purchaseOrderService;

	@Autowired
	private AuthService authService;

	// GRN UI service [/purchaseorder -- return Purchase Order UI]
	@GetMapping
	public ModelAndView purchaseOrderUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "Purchase Order | Jenaka Hardware");
		modelAndView.addObject("logusername", loggedUser.getUsername());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("purchaseorder.html");
		return modelAndView;
	}

	// get mapping for get all purchaseorder data -- [/purchaseorder/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<PurchaseOrder> findAll() {
		return purchaseOrderService.findAll();
	}

	// get mapping for find purchaseorder products by poid
	// --[/purchaseorder/findpoproductsbypoid/10]
	@GetMapping(value = "/findpoproductsbypoid/{poid}", produces = "application/json")
	public List<Product> findPOProductsByPOID(@PathVariable("poid") Integer poId) {
		return purchaseOrderService.findPOProductsByPOID(poId);
	}

	// get mapping for find POHasProduct details by poid and product id
	// --[/purchaseorder/findpoproductsbypoid/10]
	@GetMapping(value = "/findpohasproductbypoidandproductid/{poid}/{productid}", produces = "application/json")
	public POHasProduct findByPOIDAndProductId(@PathVariable("poid") Integer poId,
			@PathVariable("productid") Integer productId) {
		return purchaseOrderService.findByPOIDAndProductId(poId, productId);
	}

	// get mapping for find purchaseorder by status --
	// [/purchaseorder/getpobystatus/1]
	@GetMapping(value = "/getpobystatus/{poStatusId}", produces = "application/json")
	public List<PurchaseOrder> findPOByStatus(@PathVariable("poStatusId") Integer poStatusId) {
		return purchaseOrderService.findPOByStatus(poStatusId);
	}
	
	@GetMapping(value = "/getpobysupplier/{supplierId}", produces = "application/json")
	public List<PurchaseOrder> findPurchaseOrdersBySupplier(@PathVariable("supplierId") Integer supplierId) {
		return purchaseOrderService.findPurchaseOrdersBySupplier(supplierId);
	}

	// post mapping for save new purchase order
	@PostMapping
	public String savePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		return purchaseOrderService.savePurchaseOrder(purchaseOrder);
	}

	// put mapping for update existing purchase order
	@PutMapping
	public String updatePerchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		return purchaseOrderService.updatePurchaseOrder(purchaseOrder);
	}

	// delete mapping for delete a purchase order
	@DeleteMapping
	public String deletePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		return purchaseOrderService.deletePurchaseOrder(purchaseOrder);
	}

}
