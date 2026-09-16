

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hourglass.model.CreditTransaction" %>
<%
if (session == null || session.getAttribute("userId") == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}

Object userIdObj = session.getAttribute("userId");
int sessionUserId = 0;

if (userIdObj instanceof Number) {
    sessionUserId = ((Number) userIdObj).intValue();
} else if (userIdObj != null) {
    try {
        sessionUserId = Integer.parseInt(userIdObj.toString().trim());
    } catch (NumberFormatException e) {
        sessionUserId = 0;
    }
}

if (sessionUserId <= 0) {
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
List<CreditTransaction> transactions =
        (List<CreditTransaction>) request.getAttribute("transactions");

if (transactions == null) {
    transactions = new java.util.ArrayList<CreditTransaction>();
}
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>HourGlass — Transaction History</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/incoming-requests.css">
</head>

<body>

<div class="bg-stage"></div>

<div class="app-layout">

    <header id="app-navbar">

        <div class="navbar-left">

            <a href="${pageContext.request.contextPath}/services" class="brand-logo">
                <svg class="brand-icon" viewBox="0 0 100 100" fill="none">
                    <path d="M25 15H75V25L53 50L75 75V85H25V75L47 50L25 25V15Z"
                          stroke="#ABD2FA" stroke-width="6" stroke-linejoin="round"></path>
                    <path d="M35 25H65L50 42L35 25Z" fill="#FFAA00"></path>
                    <path d="M35 75H65L50 58L35 75Z" fill="#FFAA00"></path>
                </svg>
                <span>HourGlass</span>
            </a>

            <nav class="app-nav">
                <a href="${pageContext.request.contextPath}/services" class="nav-link">Discover</a>
                <a href="${pageContext.request.contextPath}/my-requests" class="nav-link">My Requests</a>
                <a href="${pageContext.request.contextPath}/incoming-requests" class="nav-link">Incoming Requests</a>
                <a href="${pageContext.request.contextPath}/my-services" class="nav-link">My Services</a>
                <a href="${pageContext.request.contextPath}/transactions" class="nav-link active">Transactions</a>
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
                <h1 class="page-title">Transaction History</h1>
                <p class="page-subtitle">View all your time credit transfers and earned credits.</p>
            </div>
        </div>

        <div class="results-meta">
            <span class="results-count">
                <%= transactions.size() %> transaction<%= transactions.size() == 1 ? "" : "s" %> total
            </span>
        </div>

        <section class="section-container">

            <%
            if (transactions.isEmpty()) {
            %>

                <div class="empty-state-card">
                    <div class="empty-icon-wrap">
                        <svg class="brand-icon" viewBox="0 0 100 100" fill="none" style="width: 48px; height: 48px;">
                            <path d="M25 15H75V25L53 50L75 75V85H25V75L47 50L25 25V15Z"
                                  stroke="#ABD2FA" stroke-width="6" stroke-linejoin="round"></path>
                            <path d="M35 25H65L50 42L35 25Z" fill="#FFAA00"></path>
                            <path d="M35 75H65L50 58L35 75Z" fill="#FFAA00"></path>
                        </svg>
                    </div>
                    <h3 class="empty-title">No transactions yet</h3>
                    <p class="empty-desc">Once you complete or request services and verify sessions, your credit transfers will appear here.</p>
                </div>

            <%
            } else {
            %>
                <div class="requests-grid">
            <%
                for (CreditTransaction tx : transactions) {

                    boolean isSender = (tx.getSenderId() == sessionUserId);

                    String partyName = isSender ? tx.getReceiverName() : tx.getSenderName();
                    if (partyName == null || partyName.trim().isEmpty()) {
                        partyName = "Student";
                    }
                    String partyInitial = partyName.substring(0, 1).toUpperCase();

                    String partySub = isSender ? "To: " + partyName : "From: " + partyName;

                    String statusClass = isSender ? "inactive" : "active";
                    String badgeText = isSender ? "Sent" : "Received";

                    String sign = isSender ? "-" : "+";

                    int amt = tx.getAmount();

                    String creditUnit = amt == 1 ? "credit" : "credits";
            %>

                <article class="request-card">

                    <div class="card-header-flex">
                        <div class="user-info-group">
                            <div class="requester-avatar"><%= partyInitial %></div>
                            <div class="requester-meta">
                                <h3 class="requester-name"><%= partyName %></h3>
                                <p class="requester-sub"><%= partySub %></p>
                            </div>
                        </div>

                        <span class="status-badge <%= statusClass %>"><%= badgeText %></span>
                    </div>

                    <div class="card-body">
                        <h4 class="service-title">
                            <%= tx.getServiceTitle() != null ? tx.getServiceTitle() : "Service Transfer" %>
                        </h4>

                        <% if (tx.getCreatedAt() != null) { %>
                            <p class="request-date">
                                <svg class="date-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                    <line x1="16" y1="2" x2="16" y2="6"/>
                                    <line x1="8" y1="2" x2="8" y2="6"/>
                                    <line x1="3" y1="10" x2="21" y2="10"/>
                                </svg>
                                Transferred on <%= tx.getCreatedAt() %>
                            </p>
                        <% } %>
                    </div>

                    <div class="card-footer">
                        <div class="credit-display">
                            <span class="cost-number"><%= sign %><%= amt %></span>
                            <span class="cost-label"><%= creditUnit %></span>
                        </div>
                    </div>

                </article>

            <%
                }
            %>
                </div>
            <%
            }
            %>

        </section>

    </main>

</div>

</body>

</html>
