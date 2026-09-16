package com.hourglass.controller;

import com.hourglass.dao.ServiceRequestDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/complete-request")
public class CompleteRequestServlet extends HttpServlet {

    private ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();

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
                providerId = Integer.parseInt(userIdObj.toString().trim());
            } catch (NumberFormatException e) {
                providerId = 0;
            }
        }

        if (providerId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String requestIdStr = request.getParameter("requestId");

        if (requestIdStr != null && !requestIdStr.trim().isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestIdStr.trim());
                serviceRequestDAO.completeRequest(requestId, providerId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/incoming-requests");
    }
}