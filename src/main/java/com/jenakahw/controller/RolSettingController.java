package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.RolSettingService;

@RestController
//@RequestMapping(value = "/dashboard") // add class level mapping
public class RolSettingController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private AuthService authService;;
	
	@Autowired
	private RolSettingService rolSettingService;

	@GetMapping("/rolsetting")
	public ModelAndView dashboardUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView rolSettingView = new ModelAndView();

		rolSettingView.addObject("logusername", loggedUser.getUsername());
		rolSettingView.addObject("loguserrole", userRole);
		rolSettingView.addObject("loguserphoto", loggedUser.getUserPhoto());
		rolSettingView.addObject("title", "ROL Update | Jenaka Hardware");
		rolSettingView.setViewName("rolsetting.html");

		return rolSettingView;
	}

	// put mapping for update product
	@PutMapping("/rolsetting")
	public String updateProductROL(@RequestBody String data) {
		return rolSettingService.updateProductROL(data);
	}
}
