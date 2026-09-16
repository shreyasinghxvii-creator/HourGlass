package com.hourglass.dao;

import com.hourglass.model.CreditTransaction;
import com.hourglass.util.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CreditTransactionDAO {

    public enum QrVerificationStatus {
        SUCCESS(200, "Session verified successfully. Time credits transferred to your account."),
        REQUEST_NOT_FOUND(404, "Service request not found."),
        UNAUTHORIZED_PROVIDER(403, "Unauthorized: You are not the service provider for this request."),
        INVALID_STATE(409, "Request is not in COMPLETED status."),
        ALREADY_TRANSFERRED(409, "Time credits for this request have already been transferred."),
        TOKEN_NOT_FOUND(404, "No active QR code found for this request."),
        TOKEN_EXPIRED(409, "QR code has expired. Please ask requester to generate a new one."),
        TOKEN_ALREADY_USED(409, "This QR code has already been scanned and used."),
        TOKEN_INVALID(400, "Invalid QR code token."),
        INSUFFICIENT_CREDITS(409, "Verification failed: Requester has insufficient credit balance."),
        DATABASE_ERROR(500, "An unexpected system error occurred during verification.");

        private final int httpStatusCode;
        private final String message;

        QrVerificationStatus(int httpStatusCode, String message) {
            this.httpStatusCode = httpStatusCode;
            this.message = message;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public String getMessage() {
            return message;
        }
    }

    public boolean isAlreadyTransferred(int requestId) {
        String sql = "SELECT id FROM time_transactions WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transferCredits(int requestId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String reqSql = "SELECT sr.id, sr.requester_id, sr.status, s.provider_id, s.credit_cost " +
                           "FROM service_requests sr " +
                           "JOIN services s ON sr.service_id = s.id " +
                           "WHERE sr.id = ? FOR UPDATE";

            int requesterId = -1;
            int providerId = -1;
            int creditCost = 0;
            String status = null;

            try (PreparedStatement reqStmt = conn.prepareStatement(reqSql)) {
                reqStmt.setInt(1, requestId);
                try (ResultSet rs = reqStmt.executeQuery()) {
                    if (rs.next()) {
                        requesterId = rs.getInt("requester_id");
                        providerId = rs.getInt("provider_id");
                        creditCost = rs.getInt("credit_cost");
                        status = rs.getString("status");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (!"VERIFIED".equalsIgnoreCase(status)) {
                conn.rollback();
                return false;
            }

            String checkTxSql = "SELECT id FROM time_transactions WHERE request_id = ?";
            try (PreparedStatement txStmt = conn.prepareStatement(checkTxSql)) {
                txStmt.setInt(1, requestId);
                try (ResultSet rs = txStmt.executeQuery()) {
                    if (rs.next()) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String reqUserSql = "SELECT time_credit_balance FROM users WHERE id = ? FOR UPDATE";
            int requesterBalance = 0;
            try (PreparedStatement rUserStmt = conn.prepareStatement(reqUserSql)) {
                rUserStmt.setInt(1, requesterId);
                try (ResultSet rs = rUserStmt.executeQuery()) {
                    if (rs.next()) {
                        requesterBalance = rs.getInt("time_credit_balance");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (requesterBalance < creditCost) {
                conn.rollback();
                return false;
            }

            String provUserSql = "SELECT time_credit_balance FROM users WHERE id = ? FOR UPDATE";
            try (PreparedStatement pUserStmt = conn.prepareStatement(provUserSql)) {
                pUserStmt.setInt(1, providerId);
                try (ResultSet rs = pUserStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String deductSql = "UPDATE users SET time_credit_balance = time_credit_balance - ? WHERE id = ?";
            try (PreparedStatement deductStmt = conn.prepareStatement(deductSql)) {
                deductStmt.setInt(1, creditCost);
                deductStmt.setInt(2, requesterId);
                deductStmt.executeUpdate();
            }

            String addSql = "UPDATE users SET time_credit_balance = time_credit_balance + ? WHERE id = ?";
            try (PreparedStatement addStmt = conn.prepareStatement(addSql)) {
                addStmt.setInt(1, creditCost);
                addStmt.setInt(2, providerId);
                addStmt.executeUpdate();
            }

            String insertTxSql = "INSERT INTO time_transactions (request_id, sender_id, receiver_id, amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertTxStmt = conn.prepareStatement(insertTxSql)) {
                insertTxStmt.setInt(1, requestId);
                insertTxStmt.setInt(2, requesterId);
                insertTxStmt.setInt(3, providerId);
                insertTxStmt.setInt(4, creditCost);
                insertTxStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Atomically validates QR token, marks token as used, updates request status to VERIFIED,
     * transfers credits between users, and creates audit log in a single JDBC transaction.
     */
    public QrVerificationStatus verifyAndTransferWithQr(int requestId, int providerId, String rawToken) {
        if (requestId <= 0 || providerId <= 0 || rawToken == null || rawToken.trim().isEmpty()) {
            return QrVerificationStatus.TOKEN_INVALID;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // STEP 1 & 2 & 3: LOCK SERVICE REQUEST JOINED WITH SERVICES TO OBTAIN PROVIDER_ID AND CREDIT_COST
            String reqSql = "SELECT sr.id, sr.requester_id, sr.status, s.provider_id, s.credit_cost " +
                            "FROM service_requests sr " +
                            "JOIN services s ON sr.service_id = s.id " +
                            "WHERE sr.id = ? FOR UPDATE";
            
            int requesterId;
            int creditCost;

            try (PreparedStatement reqStmt = conn.prepareStatement(reqSql)) {
                reqStmt.setInt(1, requestId);
                try (ResultSet rs = reqStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return QrVerificationStatus.REQUEST_NOT_FOUND;
                    }

                    int dbProviderId = rs.getInt("provider_id");
                    if (dbProviderId != providerId) {
                        conn.rollback();
                        return QrVerificationStatus.UNAUTHORIZED_PROVIDER;
                    }

                    requesterId = rs.getInt("requester_id");
                    if (requesterId == providerId) {
                        conn.rollback();
                        return QrVerificationStatus.UNAUTHORIZED_PROVIDER;
                    }

                    String status = rs.getString("status");
                    if ("VERIFIED".equalsIgnoreCase(status)) {
                        conn.rollback();
                        return QrVerificationStatus.ALREADY_TRANSFERRED;
                    }
                    if (!"COMPLETED".equalsIgnoreCase(status)) {
                        conn.rollback();
                        return QrVerificationStatus.INVALID_STATE;
                    }

                    creditCost = rs.getInt("credit_cost");
                    if (creditCost <= 0) {
                        conn.rollback();
                        return QrVerificationStatus.DATABASE_ERROR;
                    }
                }
            }

            // STEP 8 & 9: LOCK QR TOKEN AND VALIDATE HASH
            String tokenSql = "SELECT token_hash, used, expires_at FROM qr_tokens WHERE request_id = ? FOR UPDATE";
            try (PreparedStatement tokenStmt = conn.prepareStatement(tokenSql)) {
                tokenStmt.setInt(1, requestId);
                try (ResultSet rs = tokenStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return QrVerificationStatus.TOKEN_NOT_FOUND;
                    }

                    boolean used = rs.getBoolean("used");
                    if (used) {
                        conn.rollback();
                        return QrVerificationStatus.TOKEN_ALREADY_USED;
                    }

                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    if (expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                        conn.rollback();
                        return QrVerificationStatus.TOKEN_EXPIRED;
                    }

                    String storedHash = rs.getString("token_hash");
                    String computedHash = hashToken(rawToken.trim());
                    if (!computedHash.equalsIgnoreCase(storedHash)) {
                        conn.rollback();
                        return QrVerificationStatus.TOKEN_INVALID;
                    }
                }
            }

            // STEP 10 & 11: LOCK USER ROWS IN ASCENDING ORDER TO PREVENT DEADLOCKS
            int firstLockId = Math.min(requesterId, providerId);
            int secondLockId = Math.max(requesterId, providerId);

            int requesterBalance = -1;
            boolean providerExists = false;
            String userLockSql = "SELECT id, time_credit_balance FROM users WHERE id = ? FOR UPDATE";

            try (PreparedStatement u1Stmt = conn.prepareStatement(userLockSql)) {
                u1Stmt.setInt(1, firstLockId);
                try (ResultSet rs = u1Stmt.executeQuery()) {
                    if (rs.next()) {
                        int currentId = rs.getInt("id");
                        if (currentId == requesterId) {
                            requesterBalance = rs.getInt("time_credit_balance");
                        } else if (currentId == providerId) {
                            providerExists = true;
                        }
                    }
                }
            }

            try (PreparedStatement u2Stmt = conn.prepareStatement(userLockSql)) {
                u2Stmt.setInt(1, secondLockId);
                try (ResultSet rs = u2Stmt.executeQuery()) {
                    if (rs.next()) {
                        int currentId = rs.getInt("id");
                        if (currentId == requesterId) {
                            requesterBalance = rs.getInt("time_credit_balance");
                        } else if (currentId == providerId) {
                            providerExists = true;
                        }
                    }
                }
            }

            if (requesterBalance < 0 || !providerExists) {
                conn.rollback();
                return QrVerificationStatus.DATABASE_ERROR;
            }

            if (requesterBalance < creditCost) {
                conn.rollback();
                return QrVerificationStatus.INSUFFICIENT_CREDITS;
            }

            // STEP 12: DEDUCT REQUESTER BALANCE
            String deductSql = "UPDATE users SET time_credit_balance = time_credit_balance - ? " +
                               "WHERE id = ? AND time_credit_balance >= ?";
            try (PreparedStatement deductStmt = conn.prepareStatement(deductSql)) {
                deductStmt.setInt(1, creditCost);
                deductStmt.setInt(2, requesterId);
                deductStmt.setInt(3, creditCost);
                if (deductStmt.executeUpdate() != 1) {
                    conn.rollback();
                    return QrVerificationStatus.DATABASE_ERROR;
                }
            }

            // STEP 13: ADD CREDIT TO PROVIDER
            String addSql = "UPDATE users SET time_credit_balance = time_credit_balance + ? WHERE id = ?";
            try (PreparedStatement addStmt = conn.prepareStatement(addSql)) {
                addStmt.setInt(1, creditCost);
                addStmt.setInt(2, providerId);
                if (addStmt.executeUpdate() != 1) {
                    conn.rollback();
                    return QrVerificationStatus.DATABASE_ERROR;
                }
            }

            // STEP 14: INSERT TRANSACTION AUDIT RECORD
            String auditSql = "INSERT INTO time_transactions (request_id, sender_id, receiver_id, amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement auditStmt = conn.prepareStatement(auditSql)) {
                auditStmt.setInt(1, requestId);
                auditStmt.setInt(2, requesterId);
                auditStmt.setInt(3, providerId);
                auditStmt.setInt(4, creditCost);
                auditStmt.executeUpdate();
            }

            // STEP 15: CONSUME QR TOKEN
            String consumeTokenSql = "UPDATE qr_tokens SET used = true WHERE request_id = ? AND used = false";
            try (PreparedStatement consumeStmt = conn.prepareStatement(consumeTokenSql)) {
                consumeStmt.setInt(1, requestId);
                if (consumeStmt.executeUpdate() != 1) {
                    conn.rollback();
                    return QrVerificationStatus.TOKEN_ALREADY_USED;
                }
            }

            // STEP 16: MARK REQUEST AS VERIFIED
            String updateReqSql = "UPDATE service_requests SET status = 'VERIFIED', verified_at = NOW() " +
                                  "WHERE id = ? AND status = 'COMPLETED'";
            try (PreparedStatement updateReqStmt = conn.prepareStatement(updateReqSql)) {
                updateReqStmt.setInt(1, requestId);
                if (updateReqStmt.executeUpdate() != 1) {
                    conn.rollback();
                    return QrVerificationStatus.INVALID_STATE;
                }
            }

            // STEP 17: COMMIT ATOMIC TRANSACTION
            conn.commit();
            return QrVerificationStatus.SUCCESS;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Suppress rollback errors
                }
            }
            return QrVerificationStatus.DATABASE_ERROR;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    // Suppress connection close errors
                }
            }
        }
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    public List<CreditTransaction> getTransactionsByUser(int userId) {
        List<CreditTransaction> transactions = new ArrayList<>();
        String sql = "SELECT t.id, t.request_id, t.sender_id, t.receiver_id, t.amount, t.created_at, " +
                     "su.name AS sender_name, ru.name AS receiver_name, s.title AS service_title " +
                     "FROM time_transactions t " +
                     "JOIN users su ON t.sender_id = su.id " +
                     "JOIN users ru ON t.receiver_id = ru.id " +
                     "JOIN service_requests sr ON t.request_id = sr.id " +
                     "JOIN services s ON sr.service_id = s.id " +
                     "WHERE t.sender_id = ? OR t.receiver_id = ? " +
                     "ORDER BY t.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CreditTransaction tx = new CreditTransaction();
                    tx.setId(rs.getInt("id"));
                    tx.setRequestId(rs.getInt("request_id"));
                    tx.setSenderId(rs.getInt("sender_id"));
                    tx.setReceiverId(rs.getInt("receiver_id"));
                    tx.setAmount(rs.getInt("amount"));
                    tx.setCreatedAt(rs.getTimestamp("created_at"));

                    String sName = rs.getString("sender_name");
                    tx.setSenderName(sName != null && !sName.trim().isEmpty() ? sName : "Student");

                    String rName = rs.getString("receiver_name");
                    tx.setReceiverName(rName != null && !rName.trim().isEmpty() ? rName : "Student");

                    tx.setServiceTitle(rs.getString("service_title"));

                    transactions.add(tx);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}