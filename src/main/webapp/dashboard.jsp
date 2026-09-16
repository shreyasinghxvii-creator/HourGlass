<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hourglass.model.Service" %>
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

List<Service> services = (List<Service>) request.getAttribute("services");
if (services == null) {
    services = new java.util.ArrayList<Service>();
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass — Discover</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/dashboard.css">
    <script>
        window.contextPath = "${pageContext.request.contextPath}";
    </script>
</head>
<body>
<!-- Atmospheric Deep Navy Canvas Background -->
<div class="bg-stage"></div>

<div class="app-layout">

    <!-- UNIFIED APPLICATION NAVBAR -->
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
                <a href="${pageContext.request.contextPath}/services" class="nav-link active">Discover</a>
                <a href="${pageContext.request.contextPath}/my-requests" class="nav-link">My Requests</a>
                <a href="${pageContext.request.contextPath}/incoming-requests" class="nav-link">Incoming Requests</a>
                <a href="${pageContext.request.contextPath}/my-services" class="nav-link">My Services</a>
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

            <a href="${pageContext.request.contextPath}/logout" class="btn-logout" title="Log out" aria-label="Log out">
    <svg class="logout-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
        <polyline points="16 17 21 12 16 7"></polyline>
        <line x1="21" y1="12" x2="9" y2="12"></line>
    </svg>
    <span class="logout-text">Logout</span>
</a>
        </div>
    </header>

    <main class="dashboard-main">

        <!-- DISCOVERY HEADER -->
        <section class="discovery-header">
            <h1 class="discovery-title">What do you want to learn today?</h1>
            <p class="discovery-subtitle">Find a student who can help you with a skill, subject, or project.</p>
            
            <div class="search-bar-container">
                <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                <input type="text" class="search-input" placeholder="Search skills, subjects, or services..." aria-label="Search skills, subjects, or services">
            </div>

            <div style="margin-top: 1.25rem;">
                <a href="${pageContext.request.contextPath}/offer-service.jsp" class="btn-request offer-service-link">+ Offer a Service</a>
            </div>
        </section>

        <!-- SKILL CATEGORIES -->
        <section class="categories-section">
            <div class="category-filters">
                <button class="category-chip active">All</button>
                <button class="category-chip">Programming</button>
                <button class="category-chip">Design</button>
                <button class="category-chip">Academics</button>
                <button class="category-chip">Creative</button>
                <button class="category-chip">Career</button>
                <button class="category-chip">Other</button>
            </div>
        </section>

        <!-- FILTERS -->
        <section class="secondary-filters-section">
            <div class="filter-group">
                <label class="filter-label">Mode:</label>
                <div class="filter-options">
                    <button class="filter-chip active">All</button>
                    <button class="filter-chip">Online</button>
                    <button class="filter-chip">Offline</button>
                </div>
            </div>

            <div class="filter-group">
                <label class="filter-label">Credits:</label>
                <div class="filter-options">
                    <button class="filter-chip active">All</button>
                    <button class="filter-chip">Up to 1</button>
                    <button class="filter-chip">Up to 2</button>
                    <button class="filter-chip">Up to 3</button>
                </div>
            </div>

            <div class="filter-group sort-group">
                <label class="filter-label" for="sort-select">Sort:</label>
                <select id="sort-select" class="filter-select">
                    <option value="recommended" selected>Recommended</option>
                    <option value="newest">Newest</option>
                    <option value="most_requested">Most requested</option>
                </select>
            </div>
        </section>

        <!-- RESULTS COUNTER -->
        <div class="results-meta">
            <span class="results-count">
                <%= services.size() %> service<%= services.size() == 1 ? "" : "s" %> available
            </span>
        </div>

        <!-- SERVICE DISCOVERY GRID -->
        <section class="services-grid">

            <%
if (services.isEmpty()) {
%>
    <div class="empty-services">
        <h3>No services available yet</h3>
        <p>Be the first student to offer a skill on HourGlass.</p>
    </div>
<%
} else {
    for (Service service : services) {
        String providerName = service.getProviderName();
        if (providerName == null || providerName.trim().isEmpty()) {
            providerName = "Student";
        }
        String providerInitials = providerName.substring(0, 1).toUpperCase();

        String modeText = service.getMode();
        String modeClass = "mode-online";

        if ("Offline".equalsIgnoreCase(service.getMode())) {
            modeClass = "mode-offline";
        }

        String durationText = service.getDurationHours() + " hour";
        if (service.getDurationHours() != 1) {
            durationText += "s";
        }

        String creditText = service.getCreditCost() == 1 ? "credit" : "credits";
        long createdAtMillis = service.getCreatedAt() != null ? service.getCreatedAt().getTime() : 0L;
%>

    <article class="service-card"
             data-service-id="<%= service.getId() %>"
             data-category="<%= service.getCategory() %>"
             data-mode="<%= service.getMode() %>"
             data-credits="<%= service.getCreditCost() %>"
             data-created-at="<%= createdAtMillis %>"
             data-request-count="<%= service.getRequestCount() %>">

        <div class="card-header">
            <div class="provider-avatar"><%= providerInitials %></div>

            <div class="provider-meta">
                <h3 class="provider-name"><%= providerName %></h3>
                <p class="provider-sub"><%= service.getCategory() %> · HourGlass Student</p>
            </div>
        </div>

        <div class="card-body">
            <h4 class="service-title"><%= service.getTitle() %></h4>
            <p class="service-desc"><%= service.getDescription() %></p>
        </div>

        <div class="card-tags">
            <span class="meta-tag"><%= durationText %></span>

            <span class="meta-tag <%= modeClass %>">
                <%= modeText %>
                <% if ("Offline".equalsIgnoreCase(modeText) && service.getLocation() != null && !service.getLocation().trim().isEmpty()) { %>
                    · <%= service.getLocation() %>
                <% } %>
            </span>

            <span class="meta-tag category-tag">
                <%= service.getCategory() %>
            </span>
        </div>

        <div class="card-footer">
            <div class="credit-cost">
                <span class="cost-number"><%= service.getCreditCost() %></span>
                <span class="cost-label"><%= creditText %></span>
            </div>

            <button class="btn-request" data-service-id="<%= service.getId() %>">
                Request Session
            </button>
        </div>

    </article>

<%
    }
}
%>

        </section>

        <!-- POPULAR SKILLS SECTION -->
        <section class="popular-skills-section">
            <h2 class="popular-skills-title">Popular skills on campus</h2>
            <div class="skills-list">
                <div class="skill-pill"><span class="skill-name">Python</span></div>
                <div class="skill-pill"><span class="skill-name">Resume Review</span></div>
                <div class="skill-pill"><span class="skill-name">Java Debugging</span></div>
                <div class="skill-pill"><span class="skill-name">Exam Prep</span></div>
                <div class="skill-pill"><span class="skill-name">Public Speaking</span></div>
                <div class="skill-pill"><span class="skill-name">UI Design</span></div>
                <div class="skill-pill"><span class="skill-name">Excel</span></div>
                <div class="skill-pill"><span class="skill-name">Video Editing</span></div>
            </div>
        </section>

    </main>

</div>

<script src="${pageContext.request.contextPath}/dashboard.js"></script>
</body>
</html>