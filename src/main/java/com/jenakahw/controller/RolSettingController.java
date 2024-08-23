package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Product;
import com.jenakahw.domain.User;
import com.jenakahw.repository.ProductRepository;

@RestController
//@RequestMapping(value = "/dashboard") // add class level mapping
public class RolSettingController {
	/*
	 * Create Repository object -> Dependency injection:Repository is an interface
	 * so it cannot create instance then use dependency injection
	 */
	@Autowired
	private UserController userController;
	
	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/rolsetting")
	public ModelAndView dashboardUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedUser = userController.getLoggedUser();
		String userRole = userController.getLoggedUserRole();

		ModelAndView rolSettingView = new ModelAndView();

		rolSettingView.addObject("logusername", auth.getName());
		rolSettingView.addObject("loguserrole", userRole);
		rolSettingView.addObject("loguserphoto", loggedUser.getUserPhoto());
		rolSettingView.addObject("title", "ROL Update | Jenaka Hardware");
		rolSettingView.setViewName("rolsetting.html");

		return rolSettingView;
	}

	// put mapping for update product
	@PutMapping("/rolsetting")
	public String updateProductROL(@RequestBody String data) {
		data = data.replace("\"", "");
		System.out.println(data);

		int productId = Integer.parseInt(data.split(",")[0]);
		System.out.println(productId);
		
		Product product = productRepository.getReferenceById(productId);
		if(product == null) {
			return "Invalid Product..!";
		}

		try {
			product.setRol(Integer.parseInt(data.split(",")[1]));

			productRepository.save(product);

			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
