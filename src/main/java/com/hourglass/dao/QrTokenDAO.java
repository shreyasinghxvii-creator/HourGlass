package com.hourglass.dao;

import com.hourglass.util.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class QrTokenDAO {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a new secure QR token for a request.
     * Any previous token for the same request is replaced.
     *
     * @param requestId service request ID
     * @return raw token to be placed inside the QR code,
     *         or null if creation fails
     */
    public String createOrReplaceToken(int requestId) {

        if (requestId <= 0) {
            return null;
        }

        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        String sql =
                "INSERT INTO qr_tokens " +
                "(request_id, token_hash, expires_at, used, created_at) " +
                "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 10 MINUTE), FALSE, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "token_hash = VALUES(token_hash), " +
                "expires_at = VALUES(expires_at), " +
                "used = FALSE, " +
                "created_at = NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setString(2, tokenHash);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return rawToken;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Validates and consumes a QR token atomically.
     *
     * The token must:
     * - belong to the specified request
     * - match the stored SHA-256 hash
     * - not already be used
     * - not be expired
     *
     * If valid, it is immediately marked as used.
     *
     * @param requestId service request ID
     * @param rawToken raw token received from QR scan
     * @return true if token was valid and successfully consumed
     */
    public boolean consumeToken(int requestId, String rawToken) {

        if (requestId <= 0 || rawToken == null || rawToken.trim().isEmpty()) {
            return false;
        }

        String tokenHash = hashToken(rawToken);

        String sql =
                "UPDATE qr_tokens " +
                "SET used = TRUE " +
                "WHERE request_id = ? " +
                "AND token_hash = ? " +
                "AND used = FALSE " +
                "AND expires_at > NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setString(2, tokenHash);

            int rows = ps.executeUpdate();

            return rows == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Generates a cryptographically secure random token.
     *
     * 32 random bytes = 256 bits of entropy.
     */
    private String generateRawToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    /**
     * Creates a SHA-256 hexadecimal hash of the raw token.
     *
     * @param rawToken raw QR token
     * @return 64-character lowercase hexadecimal SHA-256 hash
     */
    private String hashToken(String rawToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes =
                    digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder(hashBytes.length * 2);

            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b & 0xff));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to hash QR token", e);
        }
    }
}