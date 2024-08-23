package com.jenakahw.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.domain.ForgetPasswordUser;
import com.jenakahw.domain.User;
import com.jenakahw.email.EmailDetails;
import com.jenakahw.email.EmailService;
import com.jenakahw.repository.UserRepository;

@RestController
public class ForgetPasswordController {
	/*
	 * Create UserRepository object -> Dependency injection:UserRepository is an
	 * interface so it cannot create instance then use dependency injection
	 */
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/forgetpassword")
	public String forgetPassword(@RequestBody ForgetPasswordUser forgetPasswordUser) {
		try {
			
			System.err.println("T1");
			User extUser = userRepository.getUserByUsernameEmail(forgetPasswordUser.getUsername(),forgetPasswordUser.getEmail());
			
			if(extUser == null) {
				System.err.println("T2");
				return "User Not Exist...";
				
			}
			System.err.println("T3");
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setSendTo(extUser.getEmail());
			emailDetails.setSubject("Jenaka Hardware User Account Password Reset");
			emailDetails.setMsgBody("This is your Temporary Password "+generteTempPW()+ " Update your password when you're logged!");
			
			
			emailService.sendSimpleMail(null);
			return "OK";
		} catch (Exception e) {
			return "Error Password Change";
		}
	}
	
	
	// generate temporary password
	public String generteTempPW() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPW = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            tempPW.append(characters.charAt(index));
        }

        return tempPW.toString();
	}
}
