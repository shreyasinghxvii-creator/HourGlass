<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
if (session == null || session.getAttribute("userId") == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}

String userName = (String) session.getAttribute("name");
if (userName == null || userName.trim().isEmpty()) {
    userName = "Student";
}

Object balanceObj = session.getAttribute("timeCreditBalance");
int creditBalance = 0;
if (balanceObj instanceof Number) {
    creditBalance = ((Number) balanceObj).intValue();
} else if (balanceObj != null) {
    try {
        creditBalance = Integer.parseInt(balanceObj.toString());
    } catch (NumberFormatException e) {
        creditBalance = 0;
    }
}

String userInitial = userName.substring(0, 1).toUpperCase();

String error = request.getParameter("error");
String success = request.getParameter("success");
String errorMessage = null;
String successMessage = null;

if (error != null) {
    if ("missing_fields".equals(error)) {
        errorMessage = "Please fill in all required fields.";
    } else if ("invalid_values".equals(error) || "invalid_numbers".equals(error)) {
        errorMessage = "Please enter valid numbers for duration and credits.";
    } else if ("invalid_user".equals(error)) {
        errorMessage = "Session user is invalid. Please log in again.";
    } else {
        errorMessage = "An error occurred while publishing your service. Please try again.";
    }
}

if ("added".equals(success)) {
    successMessage = "Your service has been successfully published!";
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass — Offer a Service</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/offer-service.css">
</head>
<body>
<div class="bg-stage"></div>

<div class="app-layout">
    <header id="app-navbar">
        <div class="navbar-left">
            <a href="${pageContext.request.contextPath}/services" class="brand-logo">
                <svg class="brand-icon" viewBox="0 0 100 100" fill="none">
                    <path d="M25 15H75V25L53 50L75 75V85H25V75L47 50L25 25V15Z"
                          stroke="#ABD2FA"
                          stroke-width="6"
                          stroke-linejoin="round"></path>
                    <path d="M35 25H65L50 42L35 25Z" fill="#FFAA00"></path>
                    <path d="M35 75H65L50 58L35 75Z" fill="#FFAA00"></path>
                </svg>
                <span>HourGlass</span>
            </a>

            <nav class="app-nav">
                <a href="${pageContext.request.contextPath}/services" class="nav-link">Discover</a>
                <a href="#requests" class="nav-link">My Requests</a>
                <a href="${pageContext.request.contextPath}/my-services.jsp" class="nav-link">My Services</a>
                <a href="#transactions" class="nav-link">Transactions</a>
            </nav>
        </div>

        <div class="navbar-right">
            <div class="balance-pill" title="Your Time Credit Balance">
                <svg class="credit-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                </svg>
                <span class="balance-value"><%= creditBalance %></span>
                <span class="balance-label"><%= creditBalance == 1 ? "Credit" : "Credits" %></span>
            </div>

            <div class="user-profile">
                <div class="user-avatar"><%= userInitial %></div>
                <span class="user-name"><%= userName %></span>
            </div>

            <a href="${pageContext.request.contextPath}/logout" class="btn-logout" title="Log out">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                    <polyline points="16 17 21 12 16 7"></polyline>
                    <line x1="21" y1="12" x2="9" y2="12"></line>
                </svg>
            </a>
        </div>
    </header>

    <main class="offer-main">
        <div class="offer-card">
            <div class="offer-header">
                <h1 class="offer-title">Offer a Service</h1>
                <p class="offer-subtitle">"Share a skill. Help a fellow student. Earn time credits."</p>
            </div>

            <% if (errorMessage != null) { %>
                <div class="alert-box alert-error"><%= errorMessage %></div>
            <% } %>

            <% if (successMessage != null) { %>
                <div class="alert-box alert-success"><%= successMessage %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/add-service" method="post" class="offer-form" id="offerServiceForm">
                <div class="form-group">
                    <label for="title" class="form-label">Service Title</label>
                    <input type="text" id="title" name="title" class="form-input" placeholder="e.g. Python Debugging Help" required>
                </div>

                <div class="form-group">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" class="form-textarea" placeholder="Describe what you can help another student with..." required></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="category" class="form-label">Category</label>
                        <select id="category" name="category" class="form-select" required>
                            <option value="">Select category</option>
                            <option value="Programming">Programming</option>
                            <option value="Design">Design</option>
                            <option value="Academics">Academics</option>
                            <option value="Creative">Creative</option>
                            <option value="Career">Career</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="mode" class="form-label">Mode</label>
                        <select id="mode" name="mode" class="form-select" required>
                            <option value="">Select mode</option>
                            <option value="Online">Online</option>
                            <option value="Offline">Offline</option>
                        </select>
                    </div>
                </div>

                <div class="form-group" id="locationGroup">
                    <label for="location" class="form-label">Location</label>
                    <input type="text" id="location" name="location" class="form-input" placeholder="e.g. Library, Room 204">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="durationHours" class="form-label">Duration</label>
                        <select id="durationHours" name="durationHours" class="form-select" required>
                            <option value="1">1 hour</option>
                            <option value="2">2 hours</option>
                            <option value="3">3 hours</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="creditCost" class="form-label">Credit Cost</label>
                        <select id="creditCost" name="creditCost" class="form-select" required>
                            <option value="1">1 credit</option>
                            <option value="2">2 credits</option>
                            <option value="3">3 credits</option>
                        </select>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-publish">Publish Service</button>
                </div>
            </form>

            <div class="offer-footer-link">
                <a href="${pageContext.request.contextPath}/my-services.jsp" class="back-link">Back to My Services</a>
            </div>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/offer-service.js"></script>
</body>
</html>