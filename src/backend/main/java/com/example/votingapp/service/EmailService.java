package com.example.votingapp.service;

import com.example.votingapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText("You have registered in the VotingCamp system. Please verify your email by clicking the following link: "
                + "http://localhost:8080/api/users/verify?email=" + user.getEmail());
        mailSender.send(message);
    }
}
