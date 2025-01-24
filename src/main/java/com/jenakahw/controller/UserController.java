package com.jenakahw.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.UserService;

@RestController
@RequestMapping(value = "/user") // class level mapping
public class UserController {

	/*
	 * Create UserRepository object -> Dependency injection:UserRepository is an
	 * interface so it cannot create instance then use dependency injection
	 */
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthService authService;

	// user UI service [/user -- return user UI]
	@GetMapping
	public ModelAndView userUI() {
		User loggedUser = authService.getLoggedUser();
		String userRole = authService.getLoggedUserRole();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "User | Jenaka Hardware");
		modelAndView.addObject("logusername", loggedUser.getUsername());
		modelAndView.addObject("loguserrole", userRole);
		modelAndView.addObject("loguserphoto", loggedUser.getUserPhoto());
		modelAndView.setViewName("user.html");
		return modelAndView;
	}

	// get mapping for get all user data -- [/user/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<User> findAll() {
		return userService.findAll();
	}

	// get mapping for get all user names and ids -- [/user/findallusers]
	@GetMapping(value = "/findallusers", produces = "application/json")
	public List<User> findAllUsers() {
		return userService.findAllUsers();
	}

	// get mapping for get logged user
	@GetMapping(value = "/loggeduser", produces = "application/json")
	public User getLoggedUserDetails() {
		User loggedUser = authService.getLoggedUser();
		loggedUser.setPassword(null);
		return loggedUser;
	}

	// post mapping for save new user
	@PostMapping
	public String saveUser(@RequestBody User user) {
		return userService.saveUser(user);
	}

	// put mapping for update user
	@PutMapping
	public String updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}

	// delete mapping for delete user account [/user]
	@DeleteMapping
	public String deleteUser(@RequestBody User user) {
		return userService.deleteUser(user);
	}

	// method for update user settings
	@PutMapping(value = "/updateprofile")
	public String updateUserSettings(@RequestBody User user) {
		return userService.updateUserSettings(user);
	}

}
