package com.amay.tom.config;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordService {

    private final Argon2 argon2;

    public PasswordService() {
        this.argon2 = Argon2Factory.create(); // defaults to Argon2id
    }

    public String hashPassword(String password) {
        return argon2.hash(2, 65536, 1, password.toCharArray());
    }

    public boolean verifyPassword(String hashed, String rawPassword) {
        return argon2.verify(hashed, rawPassword.toCharArray());
    }

    public void close() {
        argon2.wipeArray("".toCharArray());
    }
}
