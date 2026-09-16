package com.hourglass.dao;

import com.hourglass.model.ServiceRequest;
import com.hourglass.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO {

    public boolean createRequest(int serviceId, int requesterId) {
        String checkServiceSql = "SELECT provider_id, status FROM services WHERE id = ?";
        int providerId = -1;
        String serviceStatus = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkServiceSql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    providerId = rs.getInt("provider_id");
                    serviceStatus = rs.getString("status");
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (!"ACTIVE".equalsIgnoreCase(serviceStatus)) {
            return false;
        }

        if (providerId == requesterId) {
            return false;
        }

        String checkDuplicateSql = "SELECT id FROM service_requests WHERE service_id = ? AND requester_id = ? AND status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkDuplicateSql)) {
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, requesterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String insertSql = "INSERT INTO service_requests (service_id, requester_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, requesterId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ServiceRequest> getRequestsByRequester(int requesterId) {
        List<ServiceRequest> requests = new ArrayList<>();
        // PHASE 4B CHANGE: Left join with time_transactions to evaluate transferred status
        String sql = "SELECT sr.id, sr.service_id, sr.requester_id, sr.status, sr.requested_at, sr.accepted_at, sr.completed_at, sr.verified_at, " +
                     "s.title AS service_title, s.credit_cost, u.name AS provider_name, " +
                     "CASE WHEN t.id IS NOT NULL THEN 1 ELSE 0 END AS is_transferred " +
                     "FROM service_requests sr " +
                     "JOIN services s ON sr.service_id = s.id " +
                     "JOIN users u ON s.provider_id = u.id " +
                     "LEFT JOIN time_transactions t ON sr.id = t.request_id " +
                     "WHERE sr.requester_id = ? " +
                     "ORDER BY sr.requested_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requesterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ServiceRequest req = new ServiceRequest();
                    req.setId(rs.getInt("id"));
                    req.setServiceId(rs.getInt("service_id"));
                    req.setRequesterId(rs.getInt("requester_id"));
                    req.setStatus(rs.getString("status"));
                    req.setRequestedAt(rs.getTimestamp("requested_at"));
                    req.setAcceptedAt(rs.getTimestamp("accepted_at"));
                    req.setCompletedAt(rs.getTimestamp("completed_at"));
                    req.setVerifiedAt(rs.getTimestamp("verified_at"));
                    
                    req.setServiceTitle(rs.getString("service_title"));
                    req.setCreditCost(rs.getInt("credit_cost"));
                    
                    String provName = rs.getString("provider_name");
                    req.setProviderName(provName != null && !provName.trim().isEmpty() ? provName : "Student");
                    
                    // PHASE 4B CHANGE: Populate transferred field
                    req.setTransferred(rs.getInt("is_transferred") == 1);

                    requests.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // PHASE 4B CHANGE: Helper method to retrieve single request with transferred state
    public ServiceRequest getRequestById(int requestId) {
        String sql = "SELECT sr.id, sr.service_id, sr.requester_id, sr.status, sr.requested_at, sr.accepted_at, sr.completed_at, sr.verified_at, " +
                     "s.title AS service_title, s.credit_cost, u.name AS provider_name, " +
                     "CASE WHEN t.id IS NOT NULL THEN 1 ELSE 0 END AS is_transferred " +
                     "FROM service_requests sr " +
                     "JOIN services s ON sr.service_id = s.id " +
                     "JOIN users u ON s.provider_id = u.id " +
                     "LEFT JOIN time_transactions t ON sr.id = t.request_id " +
                     "WHERE sr.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ServiceRequest req = new ServiceRequest();
                    req.setId(rs.getInt("id"));
                    req.setServiceId(rs.getInt("service_id"));
                    req.setRequesterId(rs.getInt("requester_id"));
                    req.setStatus(rs.getString("status"));
                    req.setRequestedAt(rs.getTimestamp("requested_at"));
                    req.setAcceptedAt(rs.getTimestamp("accepted_at"));
                    req.setCompletedAt(rs.getTimestamp("completed_at"));
                    req.setVerifiedAt(rs.getTimestamp("verified_at"));
                    
                    req.setServiceTitle(rs.getString("service_title"));
                    req.setCreditCost(rs.getInt("credit_cost"));
                    
                    String provName = rs.getString("provider_name");
                    req.setProviderName(provName != null && !provName.trim().isEmpty() ? provName : "Student");
                    
                    req.setTransferred(rs.getInt("is_transferred") == 1);

                    return req;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ServiceRequest> getIncomingRequestsByProvider(int providerId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT sr.id, sr.service_id, sr.requester_id, sr.status, sr.requested_at, sr.accepted_at, sr.completed_at, sr.verified_at, " +
                     "s.title AS service_title, s.credit_cost, u.name AS requester_name " +
                     "FROM service_requests sr " +
                     "JOIN services s ON sr.service_id = s.id " +
                     "JOIN users u ON sr.requester_id = u.id " +
                     "WHERE s.provider_id = ? " +
                     "ORDER BY sr.requested_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, providerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ServiceRequest req = new ServiceRequest();
                    req.setId(rs.getInt("id"));
                    req.setServiceId(rs.getInt("service_id"));
                    req.setRequesterId(rs.getInt("requester_id"));
                    req.setStatus(rs.getString("status"));
                    req.setRequestedAt(rs.getTimestamp("requested_at"));
                    req.setAcceptedAt(rs.getTimestamp("accepted_at"));
                    req.setCompletedAt(rs.getTimestamp("completed_at"));
                    req.setVerifiedAt(rs.getTimestamp("verified_at"));
                    
                    req.setServiceTitle(rs.getString("service_title"));
                    req.setCreditCost(rs.getInt("credit_cost"));
                    
                    String reqName = rs.getString("requester_name");
                    req.setRequesterName(reqName != null && !reqName.trim().isEmpty() ? reqName : "Student");
                    
                    requests.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

   public boolean updateRequestStatus(int requestId, int providerId, String newStatus) {
        String updateSql;
        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            updateSql = "UPDATE service_requests sr " +
                       "JOIN services s ON sr.service_id = s.id " +
                       "SET sr.status = ?, sr.accepted_at = CURRENT_TIMESTAMP " +
                       "WHERE sr.id = ? AND s.provider_id = ? AND sr.status = 'PENDING'";
        } else if ("REJECTED".equalsIgnoreCase(newStatus)) {
            updateSql = "UPDATE service_requests sr " +
                       "JOIN services s ON sr.service_id = s.id " +
                       "SET sr.status = ? " +
                       "WHERE sr.id = ? AND s.provider_id = ? AND sr.status = 'PENDING'";
        } else {
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, newStatus);
            updateStmt.setInt(2, requestId);
            updateStmt.setInt(3, providerId);
            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   public boolean completeRequest(int requestId, int providerId) {
        String updateSql = "UPDATE service_requests sr " +
                           "JOIN services s ON sr.service_id = s.id " +
                           "SET sr.status = 'COMPLETED', sr.completed_at = CURRENT_TIMESTAMP " +
                           "WHERE sr.id = ? AND s.provider_id = ? AND sr.status = 'ACCEPTED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, requestId);
            updateStmt.setInt(2, providerId);
            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyRequest(int requestId, int requesterId) {
        String updateSql = "UPDATE service_requests " +
                           "SET status = 'VERIFIED', verified_at = CURRENT_TIMESTAMP " +
                           "WHERE id = ? AND requester_id = ? AND status = 'COMPLETED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, requestId);
            pstmt.setInt(2, requesterId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}