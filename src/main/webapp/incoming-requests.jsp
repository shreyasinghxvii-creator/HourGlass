<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hourglass.model.ServiceRequest" %>
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

@SuppressWarnings("unchecked")
List<ServiceRequest> incomingRequests = (List<ServiceRequest>) request.getAttribute("incomingRequests");
if (incomingRequests == null) {
    incomingRequests = new java.util.ArrayList<ServiceRequest>();
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass — Incoming Requests</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/incoming-requests.css">
    <!-- html5-qrcode CDN -->
    <script src="https://unpkg.com/html5-qrcode@2.3.8/html5-qrcode.min.js"></script>
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
                <a href="${pageContext.request.contextPath}/incoming-requests" class="nav-link active">Incoming Requests</a>
                <a href="${pageContext.request.contextPath}/my-services" class="nav-link">My Services</a>
                <a href="${pageContext.request.contextPath}/transactions" class="nav-link">Transactions</a>
            </nav>
        </div>

        <div class="navbar-right">
            <div class="balance-pill" title="Your Time Credit Balance">
                <svg class="credit-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 16 14"/>
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
                <h1 class="page-title">Incoming Requests</h1>
                <p class="page-subtitle">Manage students who want to learn from you.</p>
            </div>
            <div class="results-meta">
                <span class="results-count">
                    <%= incomingRequests.size() %> request<%= incomingRequests.size() == 1 ? "" : "s" %> total
                </span>
            </div>
        </div>

        <section class="section-container">
            <% if (incomingRequests.isEmpty()) { %>
            <div class="empty-state-card">
                <div class="empty-icon-wrap">
                    <svg viewBox="0 0 100 100" fill="none" class="empty-svg">
                        <path d="M25 15H75V25L53 50L75 75V85H25V75L47 50L25 25V15Z" stroke="#ABD2FA" stroke-width="6" stroke-linejoin="round"></path>
                        <path d="M35 25H65L50 42L35 25Z" fill="#FFAA00"></path>
                        <path d="M35 75H65L50 58L35 75Z" fill="#FFAA00"></path>
                    </svg>
                </div>
                <h3 class="empty-title">No incoming requests yet</h3>
                <p class="empty-desc">Requests from students interested in your services will appear here.</p>
            </div>
            <% } else { %>
            <div class="requests-grid">
                <% for (ServiceRequest req : incomingRequests) { 
                    String requester = req.getRequesterName();
                    if (requester == null || requester.trim().isEmpty()) {
                        requester = "Student";
                    }
                    String reqInitial = requester.substring(0, 1).toUpperCase();

                    String status = req.getStatus() != null ? req.getStatus().toUpperCase() : "PENDING";
                    String statusClass = "status-badge pending";

                    if ("ACCEPTED".equals(status)) {
                        statusClass = "status-badge active";
                    } else if ("COMPLETED".equals(status) || "VERIFIED".equals(status)) {
                        statusClass = "status-badge active";
                    } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                        statusClass = "status-badge inactive";
                    }

                    boolean isPending = "PENDING".equals(status);
                    boolean isAccepted = "ACCEPTED".equals(status);
                    boolean isCompleted = "COMPLETED".equals(status);
                    
                    int creditCost = req.getCreditCost();
                    String creditLabel = creditCost == 1 ? "Credit" : "Credits";
                %>
                <article class="request-card">
                    <div class="card-header-flex">
                        <div class="user-info-group">
                            <div class="requester-avatar"><%= reqInitial %></div>
                            <div class="requester-meta">
                                <h3 class="requester-name"><%= requester %></h3>
                                <p class="requester-sub">Requested a session</p>
                            </div>
                        </div>
                        <span class="<%= statusClass %>"><%= status %></span>
                    </div>

                    <div class="card-body">
                        <h4 class="service-title"><%= req.getServiceTitle() != null ? req.getServiceTitle() : "Service Request" %></h4>
                        <% if (req.getRequestedAt() != null) { %>
                            <p class="request-date">
                                <svg class="date-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                                    <line x1="16" y1="2" x2="16" y2="6"/>
                                    <line x1="8" y1="2" x2="8" y2="6"/>
                                    <line x1="3" y1="10" x2="21" y2="10"/>
                                </svg>
                                Requested on <%= req.getRequestedAt() %>
                            </p>
                        <% } %>
                    </div>

                    <div class="card-footer">
                        <div class="credit-display">
                            <span class="cost-number"><%= creditCost %></span>
                            <span class="cost-label"><%= creditLabel %></span>
                        </div>
                        
                        <div class="card-actions">
                            <% if (isPending) { %>
                                <form action="${pageContext.request.contextPath}/accept-request" method="POST" class="action-form">
                                    <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                    <button type="submit" class="btn-action accept">Accept</button>
                                </form>

                                <form action="${pageContext.request.contextPath}/reject-request" method="POST" class="action-form form-reject">
                                    <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                    <button type="submit" class="btn-action reject">Reject</button>
                                </form>
                            <% } else if (isAccepted) { %>
                                <form action="${pageContext.request.contextPath}/complete-request" method="POST" class="action-form">
                                    <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                    <button type="submit" class="btn-action accept">Complete Session</button>
                                </form>
                            <% } else if (isCompleted) { %>
                                <button type="button" 
                                        class="btn-action scan scan-qr-btn" 
                                        data-request-id="<%= req.getId() %>">
                                    <svg class="qr-btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <rect x="3" y="3" width="7" height="7"/>
                                        <rect x="14" y="3" width="7" height="7"/>
                                        <rect x="14" y="14" width="7" height="7"/>
                                        <rect x="3" y="14" width="7" height="7"/>
                                    </svg>
                                    Scan QR
                                </button>
                            <% } %>
                        </div>
                    </div>
                </article>
                <% } %>
            </div>
            <% } %>
        </section>
    </main>
</div>

<!-- QR SCANNER MODAL OVERLAY -->
<div id="qr-modal" class="qr-modal-overlay" aria-hidden="true" role="dialog" aria-labelledby="qr-modal-title">
    <div class="qr-modal-card">
        <button type="button" class="qr-modal-close" id="qr-modal-close-btn" aria-label="Close QR Scanner">&times;</button>
        
        <div class="qr-modal-header">
            <h2 id="qr-modal-title" class="qr-modal-title">Scan Session QR</h2>
            <p class="qr-modal-subtitle">Ask the requester to display their Session Verification QR code.</p>
        </div>

        <div class="qr-scanner-wrapper">
            <div id="qr-reader"></div>
        </div>

        <div id="qr-status-box" class="qr-status-box" aria-live="polite">
            <p class="qr-status-text">Align the QR code within the frame to verify completion.</p>
        </div>

        <div id="qr-modal-actions" class="qr-modal-actions">
            <button type="button" id="qr-rescan-btn" class="btn-action scan hidden">Scan Again</button>
            <button type="button" id="qr-done-btn" class="btn-action accept hidden">Done</button>
        </div>
    </div>
</div>

<script>
    window.CONTEXT_PATH = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/incoming-requests.js"></script>
</body>
</html>