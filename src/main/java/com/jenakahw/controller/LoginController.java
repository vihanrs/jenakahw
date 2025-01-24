package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;

@RestController
public class LoginController {
	/*
	 * Create UserRepository object -> Dependency injection:UserRepository is an
	 * interface so it cannot create instance then use dependency injection
	 */
	@Autowired
    private AuthService authService;
	
	@GetMapping("/login")
	public ModelAndView loginUI() {
		ModelAndView loginView = new ModelAndView();
		loginView.setViewName("login.html");
		return loginView;
	}

	@GetMapping("/error")
	public ModelAndView errorUI() {
		ModelAndView errorView = new ModelAndView();
		errorView.setViewName("error.html");
		return errorView;
	}

	@GetMapping("/dashboard")
	public ModelAndView dashboardUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView dashboardView = new ModelAndView();

		dashboardView.addObject("logusername", loggedUser.getUsername());
		dashboardView.addObject("loguserrole", userRole);
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title", "Dashboard | Jenaka Hardware");
		dashboardView.setViewName("dashboard.html");
		
		return dashboardView;
	}
}
