package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;

@RestController
//@RequestMapping(value = "/reports") // add class level mapping
public class ReportUIController {
	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	
	@Autowired
	private AuthService authService;;
	
	@GetMapping("/reportpurchaseorderui")
	public ModelAndView poReportUI() {
    	User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();
    	
		ModelAndView dashboardView = new ModelAndView();
		
		dashboardView.addObject("logusername",loggedUser.getUsername());
		dashboardView.addObject("loguserrole", userRole);
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title","Report | Jenaka Hardware");
		dashboardView.setViewName("reportpurchaseorder.html");
		return dashboardView;
	}
	
	@GetMapping("/reportgrnui")
	public ModelAndView grnReportUI() {
    	User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();
    	
		ModelAndView dashboardView = new ModelAndView();
		
		dashboardView.addObject("logusername",loggedUser.getUsername());
		dashboardView.addObject("loguserrole", userRole);
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title","Report | Jenaka Hardware");
		dashboardView.setViewName("reportgrn.html");
		return dashboardView;
	}
	
	@GetMapping("/reportsalesui")
	public ModelAndView salesReportUI() {
    	User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();
    	
		ModelAndView dashboardView = new ModelAndView();
		
		dashboardView.addObject("logusername",loggedUser.getUsername());
		dashboardView.addObject("loguserrole", userRole);
		dashboardView.addObject("loguserphoto", loggedUser.getUserPhoto());
		dashboardView.addObject("title","Report | Jenaka Hardware");
		dashboardView.setViewName("reportsales.html");
		return dashboardView;
	}
}
