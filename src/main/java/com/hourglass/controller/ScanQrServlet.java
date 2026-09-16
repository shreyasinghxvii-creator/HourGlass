package com.hourglass.controller;

import com.hourglass.dao.CreditTransactionDAO;
import com.hourglass.dao.CreditTransactionDAO.QrVerificationStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/scan-qr")
public class ScanQrServlet extends HttpServlet {

    private final CreditTransactionDAO creditTransactionDAO = new CreditTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        PrintWriter out = response.getWriter();
        out.print("{\"success\": false, \"message\": \"GET method is not supported. Use POST.\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Authentication required. Please log in.\"}");
            return;
        }

        int providerId = extractUserId(session.getAttribute("userId"));
        if (providerId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Invalid session state. Please re-login.\"}");
            return;
        }

        String requestIdParam = request.getParameter("requestId");
        String token = request.getParameter("token");

        if (requestIdParam == null || requestIdParam.trim().isEmpty() ||
            token == null || token.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Missing required parameters: requestId or token.\"}");
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdParam.trim());
            if (requestId <= 0) {
                throw new NumberFormatException("Request ID must be positive");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Invalid Request ID format.\"}");
            return;
        }

        QrVerificationStatus resultStatus = creditTransactionDAO.verifyAndTransferWithQr(
                requestId,
                providerId,
                token.trim()
        );

        response.setStatus(resultStatus.getHttpStatusCode());
        boolean isSuccess = resultStatus == QrVerificationStatus.SUCCESS;
        
        String jsonResponse = String.format(
                "{\"success\": %b, \"message\": \"%s\"}",
                isSuccess,
                escapeJson(resultStatus.getMessage())
        );

        out.print(jsonResponse);
    }

    private int extractUserId(Object userObj) {
        if (userObj instanceof Number) {
            return ((Number) userObj).intValue();
        } else if (userObj != null) {
            try {
                return Integer.parseInt(userObj.toString());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}