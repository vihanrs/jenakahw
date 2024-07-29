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

	/*
	 * Create UserRepository object -> Dependency injection:UserRepository is an
	 * interface so it cannot create instance then use dependency injection
	 */
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private PrivilegeController privilegeController;

	@Autowired
	private UserStatusRepository userStatusRepository;
	
	private static final String MODULE = "User";

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

	// get mapping for get all user data -- [/user/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<User> findAll() {
		if (privilegeController.hasPrivilege(MODULE, "select")) {
			return userRepository.findAll();
		} else {
			return null;
		}
	}

	// post mapping for save new user
	@PostMapping
	public String saveUser(@RequestBody User user) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		// check email
		User extUserByEmail = userRepository.getUserByEmail(user.getEmail());
		if (extUserByEmail != null) {
			return "Email " + user.getEmail() + " is already exist!";
		}
		// check contact
		User extUserByContact = userRepository.getUserByContact(user.getContact());
		if (extUserByContact != null) {
			return "Contact no " + user.getContact() + " is already exist!";
		}
		// check NIC
		User extUserByNIC = userRepository.getUserByNIC(user.getNic());
		if (extUserByNIC != null) {
			return "NIC " + user.getNic() + " is already exist!";
		}
		// check username
		User extUserByUsername = userRepository.getUserByUsername(user.getUsername());
		if (extUserByUsername != null) {
			return "Username " + user.getUsername() + " is already exist!";
		}

		try {
			// set auto generated employee id
			user.setEmpId(userRepository.generateNextEmpId());
			// set added date time
			user.setAddedDateTime(LocalDateTime.now());

			// encrypt password
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// put mapping for update user
	@PutMapping
	public String updateUser(@RequestBody User user) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check existing
		User extUser = userRepository.getReferenceById(user.getId());
		if (extUser == null) {
			return "User not available";
		}

		// check duplicates
		// check email
		User extUserByEmail = userRepository.getUserByEmail(user.getEmail());
		if (extUserByEmail != null && user.getId() != extUserByEmail.getId()) {
			return "Email " + extUserByEmail.getEmail() + " is already exist!";
		}

		// check contact
		User extUserByContact = userRepository.getUserByContact(user.getContact());
		if (extUserByContact != null && user.getId() != extUserByContact.getId()) {
			return "Contact no " + user.getContact() + " is already exist!";
		}

		// check NIC
		User extUserByNIC = userRepository.getUserByNIC(user.getNic());
		if (extUserByNIC != null && user.getId() != extUserByNIC.getId()) {
			return "NIC " + user.getNic() + " is already exist!";
		}

		// check username
		User extUserByUsername = userRepository.getUserByUsername(user.getUsername());
		if (extUserByUsername != null && user.getId() != extUserByUsername.getId()) {
			return "Username " + extUserByUsername.getUsername() + " is already exist!";
		}

		try {
			// set password to new user object
			user.setPassword(extUser.getPassword());
			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete user account [/user]
	@DeleteMapping
	public String deleteUser(@RequestBody User user) {
		// check privileges
		if (!privilegeController.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// need to check given user exist or not
		User extUser = userRepository.getReferenceById(user.getId());
		if (extUser == null) {
			return "Selected User Not Exist..!";
		}

		try {
			user.setUserStatusId(userStatusRepository.getReferenceById(2)); // set user status to 'Resigned'
			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// method for get logged user object
	public User getLoggedUser() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		return userRepository.getUserByUsername(auth.getName());
	}
}
