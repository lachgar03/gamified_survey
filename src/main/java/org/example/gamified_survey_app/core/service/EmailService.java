package org.example.gamified_survey_app.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    // This is a mock implementation
    // In a real application, you would inject and use JavaMailSender
    
    public void sendPasswordResetEmail(String to, String token) {
        // Build the reset URL with the token
        String resetUrl = "http://your-frontend-app/reset-password?token=" + token;
        
        // Email content
        String subject = "Password Reset Request";
        String body = "Please click the link below to reset your password:\n\n" + resetUrl;
        
        // Log the email details instead of actually sending it
        System.out.println("Password reset email sent to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        
        // In a real app, you would use:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(to);
        // message.setSubject(subject);
        // message.setText(body);
        // javaMailSender.send(message);
    }
} 