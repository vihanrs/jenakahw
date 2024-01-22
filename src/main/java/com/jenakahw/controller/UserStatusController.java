package com.jenakahw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.UserStatus;
import com.jenakahw.repository.UserStatusRepository;

@RestController
@RequestMapping(value = "/userstatus") // class level mapping
public class UserStatusController {

	@Autowired // dependency injection
	private UserStatusRepository statusRepository;

	// get mapping for get all user data -- [/user/findall]
	@GetMapping(value = "/findall", produces = "application/json")
	public List<UserStatus> findAll() {
		return statusRepository.findAll();
	}
}
