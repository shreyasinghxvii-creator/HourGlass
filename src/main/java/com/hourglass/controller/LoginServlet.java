package com.hourglass.controller;

import com.hourglass.dao.UserDAO;
import com.hourglass.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            return;
        }

        request.setAttribute("initialMode", "login");
        request.getRequestDispatcher("/auth.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.setAttribute("email", email != null ? email.trim() : "");
            request.setAttribute("initialMode", "login");
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
            return;
        }

        User user = userDAO.validateUser(email.trim(), password);

        if (user != null) {
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("name", user.getName());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("timeCreditBalance", user.getTimeCreditBalance());

            response.sendRedirect(request.getContextPath() + "/services");
        } else {
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.setAttribute("email", email.trim());
            request.setAttribute("initialMode", "login");
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
        }
    }
}