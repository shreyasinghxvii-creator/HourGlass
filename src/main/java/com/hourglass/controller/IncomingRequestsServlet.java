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

@WebServlet("/incoming-requests")
public class IncomingRequestsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = ((Number) session.getAttribute("userId")).intValue();

        if (userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserDAO userDAO = new UserDAO();
        double currentBalance = userDAO.getTimeCreditBalance(userId);
        session.setAttribute("timeCreditBalance", currentBalance);

        List<ServiceRequest> incomingRequests =
                serviceRequestDAO.getIncomingRequestsByProvider(userId);

        request.setAttribute("incomingRequests", incomingRequests);

        request.getRequestDispatcher("/incoming-requests.jsp")
               .forward(request, response);
    }
}