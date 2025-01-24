package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.GrnHasSupplierPayment;
import com.jenakahw.domain.SupplierPayment;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.SupplierPaymentService;

@RestController
//add class level mapping /supplierpayment
@RequestMapping(value = "/supplierpayment")
public class SupplierPaymentController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private SupplierPaymentService supplierPaymentService ;

	@Autowired
	private AuthService authService;

	// get mapping for generate product UI
	@GetMapping
	public ModelAndView getProductUI() {
		SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView supplierPaymentView = new ModelAndView();
		supplierPaymentView.addObject("title", "Supplier Payment  | Jenaka Hardware");
		supplierPaymentView.addObject("logusername", loggedUser.getUsername());
		supplierPaymentView.addObject("loguserrole", userRole);
		supplierPaymentView.addObject("loguserphoto", loggedUser.getUserPhoto());
		supplierPaymentView.setViewName("paymentsupplier.html");
		return supplierPaymentView;
	}

	// get mapping for get all supplier payments
	@GetMapping(value = "/findall", produces = "application/json")
	public List<SupplierPayment> findAll() {
		return supplierPaymentService.findAll();
	}

	// get mapping for get all supplier payments by added user
	@GetMapping(value = "/findallbyuser/{userid}", produces = "application/json")
	public List<SupplierPayment> findAllByUser(@PathVariable("userid") int userId) {
		return supplierPaymentService.findAllByUser(userId);
	}

	// get mapping for get all grn payments by supplier payment id
	@GetMapping(value = "/findgrnpaymentsbysupplierpayment/{supplierpaymentid}", produces = "application/json")
	public List<GrnHasSupplierPayment> findGrnPaymentsSupplierPayment(@PathVariable("supplierpaymentid") int supplierPaymentId) {
		return supplierPaymentService.findGrnPaymentsBySupplierPayment(supplierPaymentId);
	}

	// post mapping for save new supplier payment
	@PostMapping
	public String saveSupplierPayment(@RequestBody SupplierPayment supplierPayment) {
		return supplierPaymentService.saveSupplierPayment(supplierPayment);
	}
}
