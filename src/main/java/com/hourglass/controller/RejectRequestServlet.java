package com.hourglass.controller;

import com.hourglass.dao.ServiceRequestDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/reject-request")
public class RejectRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ServiceRequestDAO serviceRequestDAO =
            new ServiceRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object userIdObj = session.getAttribute("userId");

        int providerId = 0;

        if (userIdObj instanceof Number) {
            providerId = ((Number) userIdObj).intValue();
        } else if (userIdObj != null) {
            try {
                providerId = Integer.parseInt(userIdObj.toString());
            } catch (NumberFormatException e) {
                providerId = 0;
            }
        }

        String requestIdStr = request.getParameter("requestId");

        int requestId = 0;

        try {
            requestId = Integer.parseInt(requestIdStr);
        } catch (Exception e) {
            requestId = 0;
        }

        if (providerId > 0 && requestId > 0) {

            serviceRequestDAO.updateRequestStatus(
                    requestId,
                    providerId,
                    "REJECTED"
            );
        }

        response.sendRedirect(
                request.getContextPath() + "/incoming-requests"
        );
    }
}