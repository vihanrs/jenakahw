package com.jenakahw.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.Role;
import com.jenakahw.domain.User;
import com.jenakahw.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// get user by username
		User user = userRepository.getUserByUsername(username);

		if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
		
		Set<GrantedAuthority> userRoles = new HashSet<GrantedAuthority>();

		for (Role role : user.getRoles()) {
			userRoles.add(new SimpleGrantedAuthority(role.getName()));
		}
		
		// get role list from user
		ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(userRoles);
		
		boolean isActiveUser = false;
		if(user.getUserStatusId().getName().equals("Working")) {
			isActiveUser = true;
		}

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(),isActiveUser, true, true, true, grantedAuthorities);

		return userDetails;

	}

}
