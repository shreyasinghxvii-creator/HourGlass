package com.hourglass.controller;

import com.hourglass.dao.ServiceRequestDAO;
import com.hourglass.dao.UserDAO;
import com.hourglass.model.ServiceRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-requests")
public class MyRequestsServlet extends HttpServlet {
    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object userObj = session.getAttribute("userId");
        int userId = 0;

        if (userObj instanceof Number) {
            userId = ((Number) userObj).intValue();
        } else if (userObj != null) {
            try {
                userId = Integer.parseInt(userObj.toString().trim());
            } catch (NumberFormatException e) {
                userId = 0;
            }
        }

        if (userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserDAO userDAO = new UserDAO();
        double currentBalance = userDAO.getTimeCreditBalance(userId);
        session.setAttribute("timeCreditBalance", currentBalance);

        List<ServiceRequest> requests = serviceRequestDAO.getRequestsByRequester(userId);
        if (requests == null) {
            requests = new java.util.ArrayList<>();
        }

        request.setAttribute("myRequests", requests);
        request.getRequestDispatcher("/my-requests.jsp").forward(request, response);
    }
}