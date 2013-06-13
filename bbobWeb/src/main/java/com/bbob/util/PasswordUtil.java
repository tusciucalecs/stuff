package com.bbob.util;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public abstract class PasswordUtil {

    private static Md5PasswordEncoder encoder = new Md5PasswordEncoder();

    public static String encodePassword(String password) {
        return encoder.encodePassword(password, null);
    }

    public static boolean isPasswordValid(String encodedPassword,
            String rawPassword) {
        return encoder.isPasswordValid(encodedPassword, rawPassword, null);
    }

}
