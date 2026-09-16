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

@WebServlet("/add-service")
public class AddServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 3. Only allow logged-in users. Check session and userId attribute.
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 4. Get providerId from the session.
        int providerId;
        try {
            Object userIdObj = session.getAttribute("userId");
            if (userIdObj instanceof Integer) {
                providerId = (Integer) userIdObj;
            } else {
                providerId = Integer.parseInt(userIdObj.toString());
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/offer-service.jsp?error=invalid_user");
            return;
        }

        // 5. Read parameters from the form
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String mode = request.getParameter("mode");
        String location = request.getParameter("location");
        String durationHoursStr = request.getParameter("durationHours");
        String creditCostStr = request.getParameter("creditCost");

        // 6. Validate that required values are present
        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            mode == null || mode.trim().isEmpty() ||
            durationHoursStr == null || durationHoursStr.trim().isEmpty() ||
            creditCostStr == null || creditCostStr.trim().isEmpty()) {
            
            response.sendRedirect(request.getContextPath() + "/offer-service.jsp?error=missing_fields");
            return;
        }

        // 7 & 13. Safely parse durationHours and creditCost as integers, handle invalid numeric values safely.
        int durationHours;
        int creditCost;
        try {
            durationHours = Integer.parseInt(durationHoursStr.trim());
            creditCost = Integer.parseInt(creditCostStr.trim());
            
            if (durationHours <= 0 || creditCost < 0) {
                response.sendRedirect(request.getContextPath() + "/offer-service.jsp?error=invalid_values");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/offer-service.jsp?error=invalid_numbers");
            return;
        }

        // 8 & 9. Create a com.hourglass.model.Service object and set properties
        Service service = new Service();
        service.setProviderId(providerId);
        service.setTitle(title.trim());
        service.setDescription(description.trim());
        service.setCategory(category.trim());
        service.setMode(mode.trim());
        service.setLocation(location != null ? location.trim() : "");
        service.setDurationHours(durationHours);
        service.setCreditCost(creditCost);
        service.setStatus("ACTIVE");

        // 10. Call ServiceDAO().addService(service) and handle success/failure redirects
        try {
            ServiceDAO serviceDAO = new ServiceDAO();
            boolean isAdded = serviceDAO.addService(service);

            if (isAdded) {
                // 11. If successful, redirect
                response.sendRedirect(request.getContextPath() + "/my-services.jsp?success=added");
            } else {
                // 12. If unsuccessful, redirect
                response.sendRedirect(request.getContextPath() + "/offer-service.jsp?error=failed");
            }
        } catch (Exception e) {
            // Fallback error redirect in case of runtime/database exceptions
            response.sendRedirect(request.getContextPath() + "/offer-service.jsp?error=failed");
        }
    }
}