package com.hourglass.controller;

import com.hourglass.dao.QrTokenDAO;
import com.hourglass.dao.ServiceRequestDAO;
import com.hourglass.model.ServiceRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/generate-qr")
public class GenerateQrServlet extends HttpServlet {

    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
    private final QrTokenDAO qrTokenDAO = new QrTokenDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, false, 
                    "User is not authenticated.", 0, null);
            return;
        }

        Object userObj = session.getAttribute("userId");
        int sessionUserId = 0;

        if (userObj instanceof Number) {
            sessionUserId = ((Number) userObj).intValue();
        } else if (userObj != null) {
            try {
                sessionUserId = Integer.parseInt(userObj.toString().trim());
            } catch (NumberFormatException e) {
                sessionUserId = 0;
            }
        }

        if (sessionUserId <= 0) {
            sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, false, 
                    "Invalid session state.", 0, null);
            return;
        }

        String requestIdParam = request.getParameter("requestId");
        int requestId = 0;
        if (requestIdParam != null && !requestIdParam.trim().isEmpty()) {
            try {
                requestId = Integer.parseInt(requestIdParam.trim());
            } catch (NumberFormatException e) {
                requestId = 0;
            }
        }

        if (requestId <= 0) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, false, 
                    "Invalid or missing request ID.", 0, null);
            return;
        }

        ServiceRequest serviceRequest = serviceRequestDAO.getRequestById(requestId);

        if (serviceRequest == null) {
            sendJsonResponse(response, HttpServletResponse.SC_NOT_FOUND, false, 
                    "Service request not found.", 0, null);
            return;
        }

        if (serviceRequest.getRequesterId() != sessionUserId) {
            sendJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, false, 
                    "Unauthorized: You do not own this service request.", 0, null);
            return;
        }

        if (!"COMPLETED".equalsIgnoreCase(serviceRequest.getStatus())) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, false, 
                    "QR generation is only allowed for COMPLETED requests.", 0, null);
            return;
        }

        if (serviceRequest.isTransferred()) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, false, 
                    "Credits have already been transferred for this request.", 0, null);
            return;
        }

        String rawToken = qrTokenDAO.createOrReplaceToken(requestId);

        if (rawToken == null || rawToken.trim().isEmpty()) {
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, 
                    "Failed to generate QR token. Please try again.", 0, null);
            return;
        }

        sendJsonResponse(response, HttpServletResponse.SC_OK, true, 
                "Token generated successfully.", requestId, rawToken);
    }

    private void sendJsonResponse(HttpServletResponse response, int status, boolean success, 
                                  String message, int requestId, String token) throws IOException {
        response.setStatus(status);
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success).append(",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        
        if (success) {
            json.append(",");
            json.append("\"requestId\":").append(requestId).append(",");
            json.append("\"token\":\"").append(escapeJson(token)).append("\",");
            json.append("\"expiresInSeconds\":600");
        }
        
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}