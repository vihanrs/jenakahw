package com.jenakahw.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LoginController {

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
    	Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
    	
		ModelAndView dashboardView = new ModelAndView();
		dashboardView.addObject("logusername",auth.getName());
		dashboardView.addObject("title","Dashboard | Jenaka Hardware");
		dashboardView.setViewName("dashboard.html");
		return dashboardView;
	}
}
