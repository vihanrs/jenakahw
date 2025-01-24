package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Grn;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.GrnService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping(value = "/grn")
public class GrnController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private GrnService grnService;

	@Autowired
	private AuthService authService;

	// GRN UI service [/grn -- return GRN UI]
	@GetMapping
	public ModelAndView grnUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();


		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "GRN | Jenaka Hardware");
		modelAndView.addObject("logusername", loggedUser.getUsername());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("grn.html");
		return modelAndView;
	}

	// get mapping for get all grn data -- [/grn/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Grn> findAll() {
		return grnService.findAll();
	}
	
	// get mapping for get all incomplete grn data by supplier -- [/grn/findincompletebysupplier]
	@GetMapping(value = "/findincompletebysupplier/{supplierid}", produces = "application/json")
	public List<Grn> findAllIncompleteBySupplier(@PathVariable("supplierid") int supplierId) {
		return grnService.findAllIncompleteBySupplier(supplierId);
	}

	// get mapping for get all grn data by grnID -- [/grn/findbyid/5]
	@GetMapping(value = "/findbyid/{grnId}", produces = "application/json")
	public Grn findByGrnId(@PathVariable("grnId") Integer grnId) {
		return grnService.findByGrnId(grnId);
	}

	// post mapping for save grn
	@Transactional
	@PostMapping
	public String saveGrn(@RequestBody Grn grn) {
		return grnService.saveGrn(grn);
	}

	// put mapping for update existing grn
	@Transactional
	@PutMapping
	public String updateGrn(@RequestBody Grn grn) {
		return grnService.updateGrn(grn);
	}

}
