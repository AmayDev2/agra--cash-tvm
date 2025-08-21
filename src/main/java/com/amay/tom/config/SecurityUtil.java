package com.amay.tom.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    public  void passwordTest() {
        String raw = "TestUser456";
        String hashed = encodePassword(raw);

        System.out.println("Hashed: " + hashed);
        System.out.println("Matches: " + matches(raw, hashed));
    }
}
