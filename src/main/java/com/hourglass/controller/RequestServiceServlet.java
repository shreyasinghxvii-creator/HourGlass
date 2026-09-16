package com.hourglass.controller;

import com.hourglass.dao.ServiceRequestDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/request-service")
public class RequestServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // User must be logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int requesterId;

        try {
            Object userIdObj = session.getAttribute("userId");

            if (userIdObj instanceof Number) {
                requesterId = ((Number) userIdObj).intValue();
            } else {
                requesterId = Integer.parseInt(userIdObj.toString());
            }

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/services?error=invalid_user");
            return;
        }

        // Get requested service ID
        String serviceIdParam = request.getParameter("serviceId");

        if (serviceIdParam == null || serviceIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/services?error=invalid_service");
            return;
        }

        int serviceId;

        try {
            serviceId = Integer.parseInt(serviceIdParam.trim());

            if (serviceId <= 0) {
                response.sendRedirect(request.getContextPath() + "/services?error=invalid_service");
                return;
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/services?error=invalid_service");
            return;
        }

        // Prevent requesting your own service
        ServiceRequestDAO requestDAO = new ServiceRequestDAO();

        boolean created = requestDAO.createRequest(serviceId, requesterId);

        if (created) {
            response.sendRedirect(request.getContextPath() + "/services?request=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/services?request=failed");
        }
    }
}