package com.jenakahw;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.Role;
import com.jenakahw.domain.User;
import com.jenakahw.repository.RoleRepository;
import com.jenakahw.repository.UserRepository;
import com.jenakahw.repository.UserStatusRepository;

@SpringBootApplication
@RestController
public class JenakahwApplication {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserStatusRepository userStatusRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(JenakahwApplication.class, args);
		System.out.println("App Started !");
	}

	@GetMapping(value = "/createadmin")
	public String generateAdmin() {

		User extUserByUsername = userRepository.getUserByUsername("admin");
		if (extUserByUsername == null) {
			User adminUser = new User();
			adminUser.setEmpId("EMP000");
			adminUser.setFirstName("Admin");
			adminUser.setContact("0710100100");
			adminUser.setNic("980980980V");
			adminUser.setGender("Male");
			adminUser.setEmail("admin@gmail.com");
			adminUser.setUsername("admin");
			adminUser.setAddedDateTime(LocalDateTime.now());
			adminUser.setPassword(bCryptPasswordEncoder.encode("12345"));
			adminUser.setUserStatusId(userStatusRepository.getReferenceById(1));
			adminUser.setAddedDateTime(LocalDateTime.now());

			Set<Role> roles = new HashSet<Role>();
			roles.add(roleRepository.getReferenceById(1));

			adminUser.setRoles(roles);

			userRepository.save(adminUser);
		}
		return "<script> window.location.replace('http://localhost:8080/login');</script>";
	}
}
