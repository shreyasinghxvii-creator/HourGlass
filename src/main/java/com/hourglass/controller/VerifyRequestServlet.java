package com.hourglass.controller;

import com.hourglass.dao.ServiceRequestDAO;
import com.hourglass.dao.UserDAO;
import com.hourglass.model.ServiceRequest;
import com.hourglass.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/verify-request")
public class VerifyRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Prevent state modifications via GET. Redirect directly to my-requests.
        response.sendRedirect(request.getContextPath() + "/my-requests");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int requesterId = 0;
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof Number) {
            requesterId = ((Number) userIdObj).intValue();
        } else if (userIdObj != null) {
            try {
                requesterId = Integer.parseInt(userIdObj.toString());
            } catch (NumberFormatException e) {
                requesterId = 0;
            }
        }

        if (requesterId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String requestIdStr = request.getParameter("requestId");

        if (requestIdStr != null && !requestIdStr.trim().isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestIdStr.trim());
                ServiceRequest sr = serviceRequestDAO.getRequestById(requestId);

                // Enforce that the request exists AND that the logged-in user is the legitimate requester
                if (sr != null && sr.getRequesterId() == requesterId) {
                    // Verification and credit transfer bypass logic removed.
                    // QR verification and credit transfer are strictly handled via /scan-qr.
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        User updatedUser = userDAO.getUserById(requesterId);
        if (updatedUser != null) {
            session.setAttribute("timeCreditBalance", updatedUser.getTimeCreditBalance());
        }

        response.sendRedirect(request.getContextPath() + "/my-requests");
    }
}