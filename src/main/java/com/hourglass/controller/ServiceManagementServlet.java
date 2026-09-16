package com.hourglass.controller;

import com.hourglass.dao.ServiceDAO;
import com.hourglass.model.Service;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/service-management")
public class ServiceManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int providerId = 0;
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj instanceof Number) {
            providerId = ((Number) userIdObj).intValue();
        } else if (userIdObj != null) {
            try {
                providerId = Integer.parseInt(userIdObj.toString());
            } catch (NumberFormatException e) {
                providerId = 0;
            }
        }

        if (providerId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        action = action.trim();
        ServiceDAO serviceDAO = new ServiceDAO();

        if ("EDIT".equalsIgnoreCase(action)) {
            handleEditService(request, response, serviceDAO, providerId);
        } else if ("ACTIVE".equalsIgnoreCase(action) || "INACTIVE".equalsIgnoreCase(action)) {
            handleStatusChange(request, response, serviceDAO, providerId, action.toUpperCase());
        } else {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
        }
    }

    private void handleEditService(HttpServletRequest request, HttpServletResponse response, ServiceDAO serviceDAO, int providerId)
            throws IOException {

        String serviceIdStr = request.getParameter("serviceId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String mode = request.getParameter("mode");
        String location = request.getParameter("location");
        String durationHoursStr = request.getParameter("durationHours");
        String creditCostStr = request.getParameter("creditCost");

        int serviceId;
        int durationHours;
        int creditCost;

        try {
            serviceId = Integer.parseInt(serviceIdStr);
            durationHours = Integer.parseInt(durationHoursStr);
            creditCost = Integer.parseInt(creditCostStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        if (serviceId <= 0 || durationHours <= 0 || creditCost < 0) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        if (isNullOrBlank(title) || isNullOrBlank(description) || isNullOrBlank(category) || isNullOrBlank(mode)) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        Service service = new Service();
        service.setId(serviceId);
        service.setProviderId(providerId);
        service.setTitle(title.trim());
        service.setDescription(description.trim());
        service.setCategory(category.trim());
        service.setMode(mode.trim());
        service.setLocation(location != null ? location.trim() : "");
        service.setDurationHours(durationHours);
        service.setCreditCost(creditCost);

        boolean updated = serviceDAO.updateService(service);

        if (updated) {
            response.sendRedirect(request.getContextPath() + "/my-services?success=updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/my-services?error=failed");
        }
    }

    private void handleStatusChange(HttpServletRequest request, HttpServletResponse response, ServiceDAO serviceDAO, int providerId, String targetStatus)
            throws IOException {

        String serviceIdStr = request.getParameter("serviceId");
        int serviceId;

        try {
            serviceId = Integer.parseInt(serviceIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        if (serviceId <= 0) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        boolean updated = serviceDAO.updateStatus(serviceId, providerId, targetStatus);

        if (updated) {
            if ("ACTIVE".equals(targetStatus)) {
                response.sendRedirect(request.getContextPath() + "/my-services?success=activated");
            } else {
                response.sendRedirect(request.getContextPath() + "/my-services?success=deactivated");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/my-services?error=failed");
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}