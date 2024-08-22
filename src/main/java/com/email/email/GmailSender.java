package com.email.email;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

public class GmailSender {

    public List<String> readEmailAddresses(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    public void sendEmail() throws MessagingException, IOException, GeneralSecurityException {

        GmailService gmailService= new GmailService();

        Gmail service = gmailService.getGmailService();

        List<String> addresses= readEmailAddresses("addresses.txt");

//         Create email content
        for(String address: addresses) {
            MimeMessage email = createEmail(address, "email from which you want to send",
                    "Title", "body.txt","attachments");

            // Send the email
            sendMessage(service, "me", email);
        }

    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText, String fileToAttach)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        MimeMessageHelper helper = new MimeMessageHelper(email, true);
        String emailBody = new String(Files.readAllBytes(Paths.get(bodyText)));
        helper.setText(emailBody, false);
        FileSystemResource file = new FileSystemResource(new File(fileToAttach));

        helper.addAttachment(fileToAttach, file);

        return email;
    }

    private void sendMessage(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        service.users().messages().send(userId, message).execute();
    }
}

