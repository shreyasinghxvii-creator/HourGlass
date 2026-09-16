package com.hourglass.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String SCHEME_PREFIX = "pbkdf2";
    private static final int ITERATIONS = 65536;
    private static final int MAX_ITERATIONS = 1000000;
    private static final int SALT_BYTE_SIZE = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int EXPECTED_HASH_BYTES = 32;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        byte[] salt = new byte[SALT_BYTE_SIZE];
        SECURE_RANDOM.nextBytes(salt);

        char[] passwordChars = password.toCharArray();
        byte[] hash = deriveKey(passwordChars, salt, ITERATIONS, KEY_LENGTH_BITS);
        
        java.util.Arrays.fill(passwordChars, '\0');

        if (hash == null) {
            return null;
        }

        String base64Salt = Base64.getEncoder().encodeToString(salt);
        String base64Hash = Base64.getEncoder().encodeToString(hash);

        return SCHEME_PREFIX + "$" + ITERATIONS + "$" + base64Salt + "$" + base64Hash;
    }

    public static boolean verifyPassword(String password, String storedCredential) {
        if (password == null || storedCredential == null) {
            return false;
        }

        String[] parts = storedCredential.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        String scheme = parts[0];
        String iterationsStr = parts[1];
        String base64Salt = parts[2];
        String base64Hash = parts[3];

        if (!SCHEME_PREFIX.equals(scheme)) {
            return false;
        }

        int iterations;
        try {
            iterations = Integer.parseInt(iterationsStr);
        } catch (NumberFormatException e) {
            return false;
        }

        if (iterations <= 0 || iterations > MAX_ITERATIONS) {
            return false;
        }

        byte[] salt;
        byte[] expectedHash;
        try {
            salt = Base64.getDecoder().decode(base64Salt);
            expectedHash = Base64.getDecoder().decode(base64Hash);
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (salt.length != SALT_BYTE_SIZE) {
            return false;
        }

        if (expectedHash.length != EXPECTED_HASH_BYTES) {
            return false;
        }

        char[] passwordChars = password.toCharArray();
        byte[] actualHash = deriveKey(passwordChars, salt, iterations, expectedHash.length * 8);

        java.util.Arrays.fill(passwordChars, '\0');

        if (actualHash == null) {
            return false;
        }

        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private static byte[] deriveKey(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = null;
        try {
            spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
        }
    }
}