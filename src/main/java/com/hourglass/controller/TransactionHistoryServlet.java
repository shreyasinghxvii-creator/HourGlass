

package com.hourglass.controller;

import com.hourglass.dao.CreditTransactionDAO;
import com.hourglass.dao.UserDAO;
import com.hourglass.model.CreditTransaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/transactions")
public class TransactionHistoryServlet extends HttpServlet {

    private CreditTransactionDAO creditTransactionDAO = new CreditTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object userIdObj = session.getAttribute("userId");
        int userId = 0;

        if (userIdObj instanceof Number) {
            userId = ((Number) userIdObj).intValue();
        } else if (userIdObj != null) {
            try {
                userId = Integer.parseInt(userIdObj.toString().trim());
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

        List<CreditTransaction> transactions = creditTransactionDAO.getTransactionsByUser(userId);

        request.setAttribute("transactions", transactions);
        request.getRequestDispatcher("/transactions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

