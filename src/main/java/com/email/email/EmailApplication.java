package com.email.email;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class EmailApplication implements CommandLineRunner {

	public static void main(String[] args) {SpringApplication.run(EmailApplication.class, args);}

	@Override
	public void run(String... args) throws Exception {
		// Send the email using Gmail API and OAuth2
		GmailSender gmailSender= new GmailSender();
		gmailSender.sendEmail();
	}
}
