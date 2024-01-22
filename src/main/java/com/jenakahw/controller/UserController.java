package com.jenakahw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.User;
import com.jenakahw.repository.UserRepository;
import com.jenakahw.repository.UserStatusRepository;

@RestController
@RequestMapping(value = "/user") // class level mapping
public class UserController {

	// Create UserRepository object ->
	// Dependency injection:UserRepository is an interface so it cannot create
	// instance then use dependency injection
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserStatusRepository userStatusRepository;

	// user UI service [/user -- return user UI]
	@GetMapping
	public ModelAndView userUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("title", "User | Jenaka Hardware");
		modelAndView.addObject("logusername", auth.getName());
		modelAndView.setViewName("user.html");
		return modelAndView;
	}

	// get mapping for get all user data -- [/user/find all]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<User> findAll() {
		if(privilegeController.hasPrivilege("User", "select")) {
			return userRepository.findAll(Sort.by(Direction.DESC, "id"));
		}else {
			return null;
		}
	}

	// post mapping for save new user
	@PostMapping
	public String saveUser(@RequestBody User user) {
		// check authentication authorization
		if (!privilegeController.hasPrivilege("User", "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		// check email
		User extUser = userRepository.getUserByEmail(user.getEmail());
		if (extUser != null) {
			return "User Save Not Completed : Email " + user.getEmail() + " is already exist!";
		}
		// check contact
		extUser = userRepository.getUserByContact(user.getContact());
		if (extUser != null) {
			return "User Save Not Completed : Contact no " + user.getContact() + " is already exist!";
		}
		// check NIC
		extUser = userRepository.getUserByNIC(user.getNic());
		if (extUser != null) {
			return "User Save Not Completed : NIC " + user.getNic() + " is already exist!";
		}
		// check username
		extUser = userRepository.getUserByUsername(user.getUsername());
		if (extUser != null) {
			return "User Save Not Completed : Username " + user.getUsername() + " is already exist!";
		}

		try {
			user.setUserId("000002");
			// set added date time
			user.setAddedDateTime(LocalDateTime.now());
			// set is active - true
			user.setIsActive(true);

			// encrypt password
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return "User Save Not Completed : " + e.getMessage();
		}
	}

	// put mapping for update user
	@PutMapping
	public String updateUser(@RequestBody User user) {
		// check authentication authorization
		if (!privilegeController.hasPrivilege("User", "update")) {
			return "Access Denied !!!";
		}

		// check existing
		User extUser = userRepository.getReferenceById(user.getId());
		if (extUser == null) {
			return "Update not completed : User not available";
		}

		// check duplicates
		User extUserByUsername = userRepository.getUserByUsername(user.getUsername());
		if (extUserByUsername != null && user.getId() != extUserByUsername.getId()) {
			return "Update not completed :  Username " + extUserByUsername.getUsername() + " is already exist!";
		}

		User extUserByEmail = userRepository.getUserByEmail(user.getEmail());
		if (extUserByEmail != null && user.getId() != extUserByEmail.getId()) {
			return "Update not completed : Email " + extUserByEmail.getEmail() + " is already exist!";
		}

		// set isactive false when user status updated to Inactive
		if (user.getUserStatusId().getName().equals("Inative")) {
			user.setIsActive(false);
		}
		
		try {
			user.setPassword(extUser.getPassword());
			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return "User Update Not Completed : " + e.getMessage();
		}
	}

	// delete mapping for delete user account [/user]
	@DeleteMapping
	public String deleteUser(@RequestBody User user) {
		// check authentication authorization
		if (!privilegeController.hasPrivilege("User", "delete")) {
			return "Access Denied !!!";
		}

		// need to check given user exist or not
		User extUser = userRepository.getReferenceById(user.getId());
		if (extUser == null) {
			return "User Delete Not Completed : Given User Not Ext..!";
		}

		try {
			user.setIsActive(false);
			user.setUserStatusId(userStatusRepository.getReferenceById(2)); // set user status to 'Resigned'
			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return "User Delete Not Completed : " + e.getMessage();
		}
	}
}
