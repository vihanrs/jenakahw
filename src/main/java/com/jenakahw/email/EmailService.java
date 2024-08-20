package com.jenakahw.email;


public interface EmailService {
    // To send a simple email
    void sendSimpleMail(EmailDetails details);
 
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
