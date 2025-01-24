package com.jenakahw.service.impl;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.ForgetPasswordUser;
import com.jenakahw.domain.User;
import com.jenakahw.email.EmailDetails;
import com.jenakahw.email.EmailService;
import com.jenakahw.repository.UserRepository;
import com.jenakahw.service.interfaces.ForgetPasswordService;

@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService {
	// Make it final for immutability
	private final EmailService emailService;
	private final UserRepository userRepository;

	// Constructor injection
	public ForgetPasswordServiceImpl(EmailService emailService, UserRepository userRepository) {
		this.emailService = emailService;
		this.userRepository = userRepository;
	}

	@Override
	public String handleForgetPassword(ForgetPasswordUser forgetPasswordUser) {
		try {

			User extUser = userRepository.getUserByUsernameEmail(forgetPasswordUser.getUsername(),
					forgetPasswordUser.getEmail());

			if (extUser == null) {
				return "User Not Exist...";
			}
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setSendTo(extUser.getEmail());
			emailDetails.setSubject("Jenaka Hardware User Account Password Reset");
			emailDetails.setMsgBody(
					"This is your Temporary Password " + generteTempPW() + " Update your password when you're logged!");

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
