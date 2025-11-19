package com.bankingsim.ui;

import com.bankingsim.service.EmailService;

public class test {

    public static void main(String[] args) {

        String user = System.getenv("SMTP_USER");
        String pass = System.getenv("SMTP_PASS");

        System.out.println("SMTP_USER = " + user);
        System.out.println("SMTP_PASS = " + (pass == null ? "NULL" : "OK"));

        EmailService email = new EmailService(user, pass);

        email.send("yourgmail@gmail.com",
                "Test Email from Banking Simulator",
                "Hello!\nThis is a test email from Java program.\nIf you received this, SMTP works!");
    }
}
