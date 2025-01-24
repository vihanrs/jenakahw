package com.jenakahw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.ForgetPasswordUser;
import com.jenakahw.service.interfaces.ForgetPasswordService;

@RestController
public class ForgetPasswordController {
	/*
	 * Create UserRepository object -> Dependency injection:UserRepository is an
	 * interface so it cannot create instance then use dependency injection
	 */

	@Autowired
	private ForgetPasswordService forgetPasswordService;

	@PostMapping("/forgetpassword")
	public String forgetPassword(@RequestBody ForgetPasswordUser forgetPasswordUser) {
		return forgetPasswordService.handleForgetPassword(forgetPasswordUser);
	}

}
