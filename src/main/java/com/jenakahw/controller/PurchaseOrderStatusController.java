package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.PurchaseOrderStatus;
import com.jenakahw.repository.PurchaseOrderStatusRepository;

@RestController
@RequestMapping(value = "/purchaseorderstatus") // class level mapping
public class PurchaseOrderStatusController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired 
	private PurchaseOrderStatusRepository purchaseOrderStatusRepository ;

	// get mapping for get all user data -- [/purchaseorderstatus/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<PurchaseOrderStatus> findAll() {
		return purchaseOrderStatusRepository.findAll();
	}
}
