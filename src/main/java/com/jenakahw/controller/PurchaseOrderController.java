package com.jenakahw.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.POHasProduct;
import com.jenakahw.domain.PurchaseOrder;
import com.jenakahw.repository.PurchaseOrderRepository;
import com.jenakahw.repository.PurchaseOrderStatusRepository;

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
	private PurchaseOrderStatusRepository purchaseOrderStatusRepository;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserController userController;

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

	// post mapping for save new purchase order
	@PostMapping
	public String savePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeController.hasPrivilege("Purchase Order", "insert")) {
			return "Access Denied !!!";
		}

		try {
			// generate PO code

			// set added user
			purchaseOrder.setUserId(userController.getLoggedUser().getId());
			// set added date time
			purchaseOrder.setAddedDateTime(LocalDateTime.now());

			// set next pocode
			String nextPOCode = purchaseOrderRepository.getNextPOCode();
			if(nextPOCode == null) {
				//formate current date 
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
		        String formattedDate = LocalDate.now().format(formatter);
		        
		        //create new pocode for start new date
				nextPOCode = "PO"+formattedDate+"001";
			}
			
			purchaseOrder.setPoCode(nextPOCode);

			for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
				poHasProduct.setPurchaseOrderId(purchaseOrder);
			}

			purchaseOrderRepository.save(purchaseOrder);

			return "OK";
		} catch (Exception e) {
			return "Purchase Order Save Not Completed : " + e.getMessage();
		}
	}

	@DeleteMapping
	public String deletePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
		// check privileges
		if (!privilegeController.hasPrivilege("Purchase Order", "delete")) {
			return "Access Denied !!!";
		}
		
		// check existing
		PurchaseOrder extPurchaseOrder = purchaseOrderRepository.getReferenceById(purchaseOrder.getId());
		if(extPurchaseOrder == null) {
			return "Delete not completed : Purchase Order Not Exist..!";
		}
		
		try {
			//set deleted data and time
			purchaseOrder.setDeletedDateTime(LocalDateTime.now());
			
			//set deleted user id
			purchaseOrder.setDeletedUserId(userController.getLoggedUser().getId());
			
			// set Purchase Order statuts to 'Deleted'
			purchaseOrder.setPurchaseOrderStatusId(purchaseOrderStatusRepository.getReferenceById(4));
			
			for (POHasProduct poHasProduct : purchaseOrder.getPoHasProducts()) {
				poHasProduct.setPurchaseOrderId(purchaseOrder);
			}
			
			purchaseOrderRepository.save(purchaseOrder);
			
			return "OK";
		} catch (Exception e) {
			return "Purchase Order Delete Not Completed : " + e.getMessage();
		}
	}

}
