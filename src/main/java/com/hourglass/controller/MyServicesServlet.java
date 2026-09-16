package com.hourglass.controller;

import com.hourglass.dao.ServiceDAO;
import com.hourglass.dao.UserDAO;
import com.hourglass.model.Service;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-services")
public class MyServicesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = 0;
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj instanceof Number) {
            userId = ((Number) userIdObj).intValue();
        } else if (userIdObj != null) {
            try {
                userId = Integer.parseInt(userIdObj.toString());
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

        ServiceDAO serviceDAO = new ServiceDAO();
        List<Service> services = serviceDAO.getServicesByProvider(userId);
        request.setAttribute("services", services);

        request.getRequestDispatcher("/my-services.jsp").forward(request, response);
    }
}