package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;

@RestController
//@RequestMapping(value = "/reports") // add class level mapping
public class ReportUIController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	
	@Autowired
	private UserController userController;
	
	@GetMapping("/reportpurchaseorderui")
	public ModelAndView poReportUI() {
		// get logged user authentication object
    	Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
    	
    	User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();
    	
		ModelAndView dashboardView = new ModelAndView();
		
		dashboardView.addObject("logusername",auth.getName());
		dashboardView.addObject("loguserrole", userRole);
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title","Report | Jenaka Hardware");
		dashboardView.setViewName("reportpurchaseorder.html");
		return dashboardView;
	}
	
	@GetMapping("/reportgrnui")
	public ModelAndView grnReportUI() {
		// get logged user authentication object
    	Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
    	
    	User loggedUser = userController.getLoggedUser();
    	
		ModelAndView dashboardView = new ModelAndView();
		
		dashboardView.addObject("logusername",auth.getName());
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title","Report | Jenaka Hardware");
		dashboardView.setViewName("reportgrn.html");
		return dashboardView;
	}
	
	@GetMapping("/reportsalesui")
	public ModelAndView salesReportUI() {
		// get logged user authentication object
    	Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
    	
    	User loggedUser = userController.getLoggedUser();
    	
		ModelAndView dashboardView = new ModelAndView();
		
		dashboardView.addObject("logusername",auth.getName());
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title","Report | Jenaka Hardware");
		dashboardView.setViewName("reportsales.html");
		return dashboardView;
	}
}
