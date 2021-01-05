package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.models.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final UserMapper userMapper;
    private final EncryptionService encryptionService;

    public CredentialService(
            CredentialMapper credentialMapper,
            UserMapper userMapper,
            EncryptionService encryptionService
    ) {
        this.credentialMapper = credentialMapper;
        this.userMapper = userMapper;
        this.encryptionService = encryptionService;
    }

    private String getKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private String getDecryptedPassword(Credential credential) {
        String encryptedPassword = credential.getPassword();
        String key = credential.getKey();

        return encryptionService.decryptValue(encryptedPassword, key);
    }

    public Credential[] getUserCredentials(String username) {
        User user = userMapper.getUser(username);
        Credential[] userCredentials = credentialMapper.getCredentials(user.getUserId());

        for (Credential credential : userCredentials) {
            String decryptedPassword = getDecryptedPassword(credential);
            credential.setDecryptedPassword(decryptedPassword);
        }

        return userCredentials;
    }

    public int addCredential(CredentialForm credentialFields, String username) {
        User user = userMapper.getUser(username);
        String key = getKey();
        String encryptedPassword = encryptionService.encryptValue(credentialFields.getCredentialPassword(), key);

        return credentialMapper.insert(
                new Credential(null, credentialFields.getCredentialUrl(), credentialFields.getCredentialUsername(), key, encryptedPassword, user.getUserId())
        );
    }

    public int editCredential(CredentialForm credentialFields, String username) {
        User user = userMapper.getUser(username);
        Credential credential = credentialMapper.getCredential(user.getUserId(), Integer.parseInt(credentialFields.getCredentialId()));

        String key = getKey();
        String encryptedPassword = encryptionService.encryptValue(credentialFields.getCredentialPassword(), key);

        credential.setUrl(credentialFields.getCredentialUrl());
        credential.setUsername(credential.getUsername());
        credential.setKey(credential.getKey());
        credential.setDecryptedPassword(encryptedPassword);


        return credentialMapper.edit(credential);
    }

    public int deleteCredential(int credentialId, String username) {
        User user = userMapper.getUser(username);

        return credentialMapper.delete(credentialId, user.getUserId());
    }
}
