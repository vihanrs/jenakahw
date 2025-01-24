package com.jenakahw.service.impl;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Role;
import com.jenakahw.domain.User;
import com.jenakahw.repository.UserRepository;
import com.jenakahw.service.interfaces.AuthService;

@Service
public class AuthServiceImpl implements AuthService{

	private final UserRepository userRepository; // Make it final for immutability

	// Constructor injection
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	// method for get logged user object
	public User getLoggedUser() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return userRepository.getUserByUsername(auth.getName());
	}

	// method for get logged user role
	public String getLoggedUserRole() {
		User loggedUser = getLoggedUser();

		String userRole = "";

		for (Role role : loggedUser.getRoles()) {
			if (role.getName().equals("Admin")) {
				userRole = "Admin";
				break;
			} else if (role.getName().equals("Manager")) {
				userRole = "Manager";
				break;
			} else {
				userRole = role.getName();
			}
		}
		return userRole;
	}

	// method to check logged user role by given role
	public boolean isLoggedUserHasRole(String roleName) {
		// Get the logged user's roles
		Set<Role> roles = getLoggedUser().getRoles();

		for (Role role : roles) {
			if (roleName.equals(role.getName())) {
				return true;
			}
		}

		return false;
	}
}
