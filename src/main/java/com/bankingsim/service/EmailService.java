package com.bankingsim.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {

    private final Properties props = new Properties();
    private final String username;
    private final String password;

    private final boolean enabled;   // NEW → email sending ON/OFF flag

    public EmailService(String username, String password) {

        // If username/password are null OR empty → disable email sending
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {

            System.err.println("⚠ EmailService disabled: Missing SMTP credentials");
            this.enabled = false;

        } else {
            this.enabled = true;
        }

        this.username = (username == null ? "" : username);
        this.password = (password == null ? "" : password);

        // SMTP config
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void send(String to, String subject, String body) {

        // If disabled → do nothing, avoid crash
        if (!enabled) {
            System.err.println("⚠ EmailService: Email not sent (SMTP disabled)");
            return;
        }

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);
            System.out.println("Email sent to " + to + " | subject=" + subject);

        } catch (Exception e) {
            System.err.println("❌ EmailService error: " + e.getMessage());
        }
    }
}
