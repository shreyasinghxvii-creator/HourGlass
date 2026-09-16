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

@WebServlet("/edit-service")
public class EditServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        String serviceIdStr = request.getParameter("id");
        int serviceId = 0;

        try {
            if (serviceIdStr != null) {
                serviceId = Integer.parseInt(serviceIdStr.trim());
            }
        } catch (NumberFormatException e) {
            serviceId = 0;
        }

        if (serviceId <= 0) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=invalid");
            return;
        }

        ServiceDAO serviceDAO = new ServiceDAO();
        Service service = serviceDAO.getServiceById(serviceId);

        if (service == null) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=notfound");
            return;
        }

        if (service.getProviderId() != providerId) {
            response.sendRedirect(request.getContextPath() + "/my-services?error=unauthorized");
            return;
        }

        request.setAttribute("service", service);
        request.getRequestDispatcher("/edit-service.jsp").forward(request, response);
    }
}