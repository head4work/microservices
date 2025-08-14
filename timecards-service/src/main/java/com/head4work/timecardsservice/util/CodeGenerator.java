package com.head4work.timecardsservice.util;

import java.security.SecureRandom;
import java.util.Base64;

public class CodeGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate6DigitCode() {
        int code = RANDOM.nextInt(900_000) + 100_000; // ensures 100000–999999
        return String.valueOf(code);
    }

    public static String generateToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}
