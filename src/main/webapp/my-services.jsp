<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hourglass.model.Service" %>
<%
if (session == null || session.getAttribute("userId") == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}

int providerId = 0;
Object userIdObj = session.getAttribute("userId");
if (userIdObj instanceof Integer) {
    providerId = (Integer) userIdObj;
} else if (userIdObj instanceof Number) {
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

@SuppressWarnings("unchecked")
List<Service> services = (List<Service>) request.getAttribute("services");
if (services == null) {
    services = new ArrayList<Service>();
}

int activeCount = 0;
int totalHours = 0;
int totalCredits = 0;
for (Service s : services) {
    if ("ACTIVE".equalsIgnoreCase(s.getStatus()) || s.getStatus() == null) {
        activeCount++;
    }
    totalHours += s.getDurationHours();
    totalCredits += s.getCreditCost();
}
double avgCredit = services.isEmpty() ? 0.0 : ((double) totalCredits / services.size());
String avgCreditStr = String.format("%.1f", avgCredit);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass — My Services</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/my-services.css">
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
                <a href="${pageContext.request.contextPath}/my-requests" class="nav-link">My Requests</a>
                <a href="${pageContext.request.contextPath}/incoming-requests" class="nav-link">Incoming Requests</a>
                <a href="${pageContext.request.contextPath}/my-services" class="nav-link active">My Services</a>
                <a href="${pageContext.request.contextPath}/transactions" class="nav-link">Transactions</a>
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
        </div>
    </header>

    <main class="myservices-main">
        <div class="page-header-flex">
            <div>
                <h1 class="page-title">My Services</h1>
                <p class="page-subtitle">Share what you know. Help another student. Earn time credits.</p>
            </div>
            <a href="${pageContext.request.contextPath}/offer-service.jsp" class="btn-primary-glow">+ Offer a Service</a>
        </div>

        <% if (!services.isEmpty()) { %>
        <section class="stats-grid">
            <div class="stat-card">
                <span class="stat-label">Active Services</span>
                <span class="stat-value"><%= activeCount %></span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Total Hours Offered</span>
                <span class="stat-value"><%= totalHours %></span>
            </div>
            <div class="stat-card">
                <span class="stat-label">Avg. Credit Cost</span>
                <span class="stat-value"><%= avgCreditStr %></span>
            </div>
        </section>
        <% } %>

        <section class="section-container">
            <h2 class="section-heading">Your offerings</h2>

            <% if (services.isEmpty()) { %>
            <div class="empty-state-card">
                <div class="empty-icon-wrap">
                    <svg viewBox="0 0 100 100" fill="none" class="empty-svg">
                        <path d="M25 15H75V25L53 50L75 75V85H25V75L47 50L25 25V15Z" stroke="#ABD2FA" stroke-width="6" stroke-linejoin="round"></path>
                        <path d="M35 25H65L50 42L35 25Z" fill="#FFAA00"></path>
                        <path d="M35 75H65L50 58L35 75Z" fill="#FFAA00"></path>
                    </svg>
                </div>
                <h3 class="empty-title">No services yet</h3>
                <p class="empty-desc">Offer your first skill and start contributing to the campus community.</p>
                <a href="${pageContext.request.contextPath}/offer-service.jsp" class="btn-primary-glow">+ Offer a Service</a>
            </div>
            <% } else { %>
            <div class="services-grid">
                <% for (Service s : services) { 
                    String modeText = s.getMode();
                    String modeClass = "mode-online";
                    if ("Offline".equalsIgnoreCase(modeText)) {
                        modeClass = "mode-offline";
                    }
                    boolean isActive = "ACTIVE".equalsIgnoreCase(s.getStatus()) || s.getStatus() == null;
                    String statusText = isActive ? "ACTIVE" : "INACTIVE";
                    String statusClass = isActive ? "status-badge active" : "status-badge inactive";
                    String durationText = s.getDurationHours() + " hour" + (s.getDurationHours() != 1 ? "s" : "");
                    String creditText = s.getCreditCost() == 1 ? "Credit" : "Credits";
                %>
                <article class="service-card">
                    <div class="card-top-badges">
                        <span class="badge category-badge"><%= s.getCategory() %></span>
                        <span class="badge <%= statusClass %>"><%= statusText %></span>
                    </div>

                    <div class="card-body">
                        <h3 class="service-title"><%= s.getTitle() %></h3>
                        <p class="service-desc"><%= s.getDescription() %></p>
                    </div>

                    <div class="card-meta-row">
                        <span class="meta-pill <%= modeClass %>">
                            <%= modeText %>
                            <% if ("Offline".equalsIgnoreCase(modeText) && s.getLocation() != null && !s.getLocation().trim().isEmpty()) { %>
                                · <%= s.getLocation() %>
                            <% } %>
                        </span>
                        <span class="meta-pill duration-pill"><%= durationText %></span>
                    </div>

                    <div class="card-footer">
                        <div class="credit-display">
                            <span class="cost-number"><%= s.getCreditCost() %></span>
                            <span class="cost-label"><%= creditText %></span>
                        </div>
                        <div class="card-actions">
                            <a href="${pageContext.request.contextPath}/edit-service?id=<%= s.getId() %>" class="btn-action edit">Edit</a>
                            <form action="${pageContext.request.contextPath}/service-management" method="post">
                                <input type="hidden" name="serviceId" value="<%= s.getId() %>">
                                <input type="hidden" name="action" value="<%= isActive ? "INACTIVE" : "ACTIVE" %>">
                                <button type="submit" class="btn-action toggle"><%= isActive ? "Deactivate" : "Reactivate" %></button>
                            </form>
                        </div>
                    </div>
                </article>
                <% } %>
            </div>
            <% } %>
        </section>
    </main>
</div>

<script src="${pageContext.request.contextPath}/my-services.js"></script>
</body>
</html>