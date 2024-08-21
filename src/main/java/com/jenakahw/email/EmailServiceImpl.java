package com.jenakahw.email;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
//
//@EnableAsync
//@Service
//public class EmailServiceImpl implements EmailService {
//	@Autowired
//	private JavaMailSender javaMailSender;
//
//	@Value("${spring.mail.username}")
//	private String sender;
//
//	// To send a simple email
//	@Async
//	public void sendSimpleMail(EmailDetails details) {
//
//		// Creating a simple mail message
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//
//		// Setting up necessary details
//		mailMessage.setFrom(sender);
//		mailMessage.setTo(details.getSendTo());
//		mailMessage.setText(details.getMsgBody());
//		mailMessage.setSubject(details.getSubject());
//
//		// Sending the mail
//		javaMailSender.send(mailMessage);
//		
//		System.out.println("Mail sent Successfully");
//
//	}
//
//	// To send an email with attachment
//	public String sendMailWithAttachment(EmailDetails details) {
//		// Creating a mime message
//		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//		MimeMessageHelper mimeMessageHelper;
//
//		try {
//
//			// Setting multipart as true for attachments to be send
//			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//			mimeMessageHelper.setFrom(sender);
//			mimeMessageHelper.setTo(details.getSendTo());
//			mimeMessageHelper.setText(details.getMsgBody());
//			mimeMessageHelper.setSubject(details.getSubject());
//
//			// Adding the attachment
//			FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
//
//			mimeMessageHelper.addAttachment(file.getFilename(), file);
//
//			// Sending the mail
//			javaMailSender.send(mimeMessage);
//			return "Mail sent Successfully";
//			
//		}catch (MessagingException e) {
//			// Display message when exception occurred
//			return "Error while sending mail!!!";
//		}
//	}
//
//}
