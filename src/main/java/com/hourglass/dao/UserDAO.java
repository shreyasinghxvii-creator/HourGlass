package com.hourglass.dao;

import com.hourglass.model.User;
import com.hourglass.util.DBConnection;
import com.hourglass.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) {
        if (user == null || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return false;
        }

        String passwordHash = PasswordUtil.hashPassword(user.getPassword());
        if (passwordHash == null) {
            return false;
        }

        String sql = "INSERT INTO users (name, email, password, time_credit_balance) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, passwordHash);
            stmt.setDouble(4, user.getTimeCreditBalance() > 0 ? user.getTimeCreditBalance() : 3.0);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User validateUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT id, name, email, password, time_credit_balance FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String name = rs.getString("name");
                    String userEmail = rs.getString("email");
                    String storedPassword = rs.getString("password");
                    double balance = rs.getDouble("time_credit_balance");

                    if (storedPassword != null && storedPassword.startsWith("pbkdf2$")) {
                        boolean valid = PasswordUtil.verifyPassword(password, storedPassword);
                        if (valid) {
                            return new User(userId, name, userEmail, storedPassword, balance);
                        }
                    } else {
                        if (storedPassword != null && storedPassword.equals(password)) {
                            String newHash = PasswordUtil.hashPassword(password);
                            if (newHash == null) {
                                return null;
                            }

                            boolean upgraded = upgradePasswordHash(userId, newHash);
                            if (!upgraded) {
                                return null;
                            }

                            return new User(userId, name, userEmail, newHash, balance);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean upgradePasswordHash(int userId, String newHash) {
        String updateSql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT id, name, email, password, time_credit_balance FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getDouble("time_credit_balance")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getTimeCreditBalance(int userId) {
        String sql = "SELECT time_credit_balance FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("time_credit_balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}