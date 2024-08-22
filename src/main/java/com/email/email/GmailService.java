package com.email.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GmailService {
    private static String applicationName= "email";

    private String credentialsFilePath = "creds file path";

    private String tokensDirectoryPath= "token path";

    private int redirectPort= 8888;

    private String gmailScopes= "https://www.googleapis.com/auth/gmail.send";

    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public Credential getCredentials() throws IOException, GeneralSecurityException {

        System.out.println(credentialsFilePath);

        Path filePath = Paths.get(credentialsFilePath);

        // Check if file exists
        if (!Files.exists(filePath)) {
            throw new IOException("Credentials file not found at " + filePath.toAbsolutePath().toString());
        }

        // Load the credentials
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new FileReader(filePath.toFile()));

        List<String> scopes = Collections.singletonList(gmailScopes);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get(tokensDirectoryPath).toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(redirectPort).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Gmail getGmailService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();
        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }
}
