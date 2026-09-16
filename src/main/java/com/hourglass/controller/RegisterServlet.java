package com.hourglass.controller;

import com.hourglass.dao.UserDAO;
import com.hourglass.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("initialMode", "register");
        request.getRequestDispatcher("/auth.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String usernameParam = request.getParameter("username");
        String emailParam = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        String name = usernameParam != null ? usernameParam.trim() : "";
        String email = emailParam != null ? emailParam.trim() : "";

        if (name.isEmpty() || email.isEmpty() || password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.setAttribute("regUsername", name);
            request.setAttribute("regEmail", email);
            request.setAttribute("initialMode", "register");
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.setAttribute("regUsername", name);
            request.setAttribute("regEmail", email);
            request.setAttribute("initialMode", "register");
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
            return;
        }

        if (userDAO.isEmailExists(email)) {
            request.setAttribute("errorMessage", "An account with this email already exists.");
            request.setAttribute("regUsername", name);
            request.setAttribute("regEmail", email);
            request.setAttribute("initialMode", "register");
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
            return;
        }

        User newUser = new User(name, email, password);

        boolean registered = userDAO.registerUser(newUser);

        if (registered) {
            request.setAttribute("initialMode", "login");
            request.setAttribute("successMessage", "Account created successfully! Please log in.");
            request.setAttribute("loginEmail", email);
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.setAttribute("regUsername", name);
            request.setAttribute("regEmail", email);
            request.setAttribute("initialMode", "register");
            request.getRequestDispatcher("/auth.jsp").forward(request, response);
        }
    }
}