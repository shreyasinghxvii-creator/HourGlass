package com.hourglass.dao;

import com.hourglass.model.Service;
import com.hourglass.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public boolean addService(Service service) {
        String sql = "INSERT INTO services (provider_id, title, description, category, mode, location, duration_hours, credit_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, service.getProviderId());
            pstmt.setString(2, service.getTitle());
            pstmt.setString(3, service.getDescription());
            pstmt.setString(4, service.getCategory());
            pstmt.setString(5, service.getMode());
            pstmt.setString(6, service.getLocation());
            pstmt.setInt(7, service.getDurationHours());
            pstmt.setInt(8, service.getCreditCost());
            pstmt.setString(9, service.getStatus() != null ? service.getStatus() : "ACTIVE");

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Service> getAllActiveServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT s.id, s.provider_id, s.title, s.description, s.category, s.mode, s.location, s.duration_hours, s.credit_cost, s.status, s.created_at, u.name AS provider_name, " +
                     "COALESCE(rc.req_count, 0) AS request_count " +
                     "FROM services s " +
                     "JOIN users u ON s.provider_id = u.id " +
                     "LEFT JOIN (SELECT service_id, COUNT(*) AS req_count FROM service_requests GROUP BY service_id) rc ON s.id = rc.service_id " +
                     "WHERE s.status = 'ACTIVE' " +
                     "ORDER BY s.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapService(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public List<Service> getServicesByProvider(int providerId) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT s.id, s.provider_id, s.title, s.description, s.category, s.mode, s.location, s.duration_hours, s.credit_cost, s.status, s.created_at, u.name AS provider_name, " +
                     "COALESCE(rc.req_count, 0) AS request_count " +
                     "FROM services s " +
                     "JOIN users u ON s.provider_id = u.id " +
                     "LEFT JOIN (SELECT service_id, COUNT(*) AS req_count FROM service_requests GROUP BY service_id) rc ON s.id = rc.service_id " +
                     "WHERE s.provider_id = ? " +
                     "ORDER BY s.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, providerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapService(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public Service getServiceById(int serviceId) {
        String sql = "SELECT s.id, s.provider_id, s.title, s.description, s.category, s.mode, s.location, s.duration_hours, s.credit_cost, s.status, s.created_at, u.name AS provider_name, " +
                     "COALESCE(rc.req_count, 0) AS request_count " +
                     "FROM services s " +
                     "JOIN users u ON s.provider_id = u.id " +
                     "LEFT JOIN (SELECT service_id, COUNT(*) AS req_count FROM service_requests GROUP BY service_id) rc ON s.id = rc.service_id " +
                     "WHERE s.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapService(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateService(Service service) {
        String sql = "UPDATE services " +
                     "SET title = ?, description = ?, category = ?, mode = ?, location = ?, duration_hours = ?, credit_cost = ? " +
                     "WHERE id = ? AND provider_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getTitle());
            pstmt.setString(2, service.getDescription());
            pstmt.setString(3, service.getCategory());
            pstmt.setString(4, service.getMode());
            pstmt.setString(5, service.getLocation());
            pstmt.setInt(6, service.getDurationHours());
            pstmt.setInt(7, service.getCreditCost());
            pstmt.setInt(8, service.getId());
            pstmt.setInt(9, service.getProviderId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int serviceId, int providerId, String status) {
        if (!"ACTIVE".equalsIgnoreCase(status) && !"INACTIVE".equalsIgnoreCase(status)) {
            return false;
        }

        String sql = "UPDATE services SET status = ? WHERE id = ? AND provider_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toUpperCase());
            pstmt.setInt(2, serviceId);
            pstmt.setInt(3, providerId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Service mapService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setProviderId(rs.getInt("provider_id"));
        service.setTitle(rs.getString("title"));
        service.setDescription(rs.getString("description"));
        service.setCategory(rs.getString("category"));
        service.setMode(rs.getString("mode"));
        service.setLocation(rs.getString("location"));
        service.setDurationHours(rs.getInt("duration_hours"));
        service.setCreditCost(rs.getInt("credit_cost"));
        service.setStatus(rs.getString("status"));
        service.setCreatedAt(rs.getTimestamp("created_at"));
        
        try {
            service.setProviderName(rs.getString("provider_name"));
        } catch (SQLException e) {
            service.setProviderName(null);
        }

        try {
            service.setRequestCount(rs.getInt("request_count"));
        } catch (SQLException e) {
            service.setRequestCount(0);
        }

        return service;
    }
}