package com.amay.tom.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordService {

    private static final String STATIC_SALT = Base64.getEncoder().encodeToString("4c369eb54b2f30d050157ac174a5f75de7d3bb6a8a49e3e4d9ffa5aa3912cba2".getBytes(StandardCharsets.UTF_8));

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] input = (password + STATIC_SALT).getBytes(StandardCharsets.UTF_8);
            byte[] hash = digest.digest(input);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public boolean verifyPassword(String hashed, String rawPassword) {
        String newHash = hashPassword(rawPassword);
        return newHash.equals(hashed);
    }
}
