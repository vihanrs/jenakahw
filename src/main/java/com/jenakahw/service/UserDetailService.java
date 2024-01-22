package com.jenakahw.service;

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

		Set<GrantedAuthority> userRoles = new HashSet<GrantedAuthority>();

		// get role list from user
		for (Role role : user.getRoles()) {
			userRoles.add(new SimpleGrantedAuthority(role.getName()));
		}

		ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(userRoles);
		
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.getIsActive(), true, true, true, grantedAuthorities);

		return userDetails;
	}

}
