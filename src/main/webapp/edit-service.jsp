<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hourglass.model.Service" %>
<%
if (session == null || session.getAttribute("userId") == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}

Service service = (Service) request.getAttribute("service");
if (service == null) {
    response.sendRedirect(request.getContextPath() + "/my-services?error=notfound");
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

// Helper method for HTML escaping within scriptlet scope
class HtmlUtil {
    public String escape(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
HtmlUtil html = new HtmlUtil();

String serviceCategory = service.getCategory() != null ? service.getCategory() : "";
String serviceMode = service.getMode() != null ? service.getMode() : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass — Edit Service</title>
    <style>
        :root {
            --bg-dark: #111844;
            --card-bg: rgba(18, 26, 60, 0.65);
            --card-border: rgba(171, 210, 250, 0.15);
            --text-main: #ABD2FA;
            --text-sub: rgba(171, 210, 250, 0.7);
            --text-white: #ffffff;
            --accent-cyan: #ABD2FA;
            --accent-violet: #DC95FF;
            --accent-amber: #FFAA00;
            --input-bg: rgba(10, 15, 40, 0.6);
            --input-border: rgba(171, 210, 250, 0.2);
            --input-focus: #DC95FF;
            --glow-cyan: rgba(171, 210, 250, 0.25);
            --glow-violet: rgba(220, 149, 255, 0.3);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            background-color: var(--bg-dark);
            background-image: 
                radial-gradient(circle at 15% 20%, rgba(220, 149, 255, 0.08) 0%, transparent 40%),
                radial-gradient(circle at 85% 80%, rgba(171, 210, 250, 0.08) 0%, transparent 40%);
            background-attachment: fixed;
            color: var(--text-main);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Inter, Helvetica, Arial, sans-serif;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            padding-top: 112px; /* 72px navbar height + 40px top clearance */
        }

        .app-layout {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px 40px 20px;
            width: 100%;
        }

        /* Fixed 72px Glass Navbar */
        #app-navbar {
            height: 72px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 24px;
            background: rgba(17, 24, 68, 0.75);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border-bottom: 1px solid var(--card-border);
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            z-index: 100;
        }

        .navbar-left, .navbar-right {
            display: flex;
            align-items: center;
            gap: 24px;
        }

        .brand-logo {
            display: flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
            color: var(--text-white);
            font-weight: 700;
            font-size: 1.25rem;
            letter-spacing: 0.5px;
        }

        .brand-icon {
            width: 28px;
            height: 28px;
        }

        .app-nav {
            display: flex;
            gap: 8px;
        }

        .nav-link {
            color: var(--text-sub);
            text-decoration: none;
            font-size: 0.95rem;
            font-weight: 500;
            padding: 22px 12px;
            position: relative;
            transition: color 0.2s ease;
        }

        .nav-link:hover {
            color: var(--text-white);
        }

        .nav-link.active {
            color: var(--accent-cyan);
            font-weight: 600;
        }

        .nav-link.active::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            height: 3px;
            background-color: var(--accent-cyan);
            border-radius: 3px 3px 0 0;
            box-shadow: 0 -2px 8px var(--glow-cyan);
        }

        .balance-pill {
            display: flex;
            align-items: center;
            gap: 8px;
            background: rgba(255, 170, 0, 0.12);
            border: 1px solid rgba(255, 170, 0, 0.35);
            padding: 6px 14px;
            border-radius: 20px;
            color: var(--accent-amber);
            font-weight: 600;
            font-size: 0.9rem;
            box-shadow: 0 0 12px rgba(255, 170, 0, 0.15);
        }

        .credit-icon {
            width: 16px;
            height: 16px;
        }

        .user-profile {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .user-avatar {
            width: 34px;
            height: 34px;
            background: linear-gradient(135deg, var(--accent-violet), var(--accent-cyan));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            color: #111844;
            font-size: 0.9rem;
            box-shadow: 0 0 10px var(--glow-violet);
        }

        .user-name {
            color: var(--text-white);
            font-weight: 500;
            font-size: 0.95rem;
        }

        /* Glass Form Container */
        .form-container {
            max-width: 680px;
            margin: 0 auto;
            background: var(--card-bg);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid var(--card-border);
            border-radius: 12px;
            padding: 36px;
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4), 0 0 20px rgba(171, 210, 250, 0.03);
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--accent-cyan);
            text-decoration: none;
            font-size: 0.9rem;
            font-weight: 500;
            margin-bottom: 24px;
            transition: all 0.2s ease;
        }

        .back-link:hover {
            color: var(--text-white);
            transform: translateX(-3px);
        }

        .form-title {
            font-size: 1.6rem;
            font-weight: 700;
            margin-bottom: 6px;
            color: var(--text-white);
            letter-spacing: 0.3px;
        }

        .form-subtitle {
            color: var(--text-sub);
            font-size: 0.95rem;
            margin-bottom: 28px;
        }

        .form-group {
            margin-bottom: 22px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        label {
            display: block;
            font-size: 0.875rem;
            font-weight: 600;
            margin-bottom: 8px;
            color: var(--text-main);
        }

        input[type="text"],
        input[type="number"],
        select,
        textarea {
            width: 100%;
            padding: 12px 16px;
            background-color: var(--input-bg);
            border: 1px solid var(--input-border);
            border-radius: 8px;
            color: var(--text-white);
            font-size: 0.95rem;
            font-family: inherit;
            transition: all 0.25s ease;
        }

        select {
            appearance: none;
            background-image: url("data:image/svg+xml;utf8,<svg fill='%23ABD2FA' height='24' viewBox='0 0 24 24' width='24' xmlns='http://www.w3.org/2000/svg'><path d='M7 10l5 5 5-5z'/></svg>");
            background-repeat: no-repeat;
            background-position: right 12px center;
            padding-right: 40px;
        }

        select option {
            background-color: #111844;
            color: var(--text-white);
        }

        input[type="text"]:focus,
        input[type="number"]:focus,
        select:focus,
        textarea:focus {
            outline: none;
            border-color: var(--input-focus);
            box-shadow: 0 0 12px var(--glow-violet);
            background-color: rgba(10, 15, 40, 0.85);
        }

        textarea {
            resize: vertical;
            min-height: 110px;
        }

        .form-actions {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 14px;
            margin-top: 32px;
            padding-top: 24px;
            border-top: 1px solid var(--card-border);
        }

        .btn-secondary {
            padding: 10px 22px;
            background-color: transparent;
            border: 1px solid var(--input-border);
            color: var(--text-main);
            border-radius: 8px;
            text-decoration: none;
            font-size: 0.95rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.25s ease;
        }

        .btn-secondary:hover {
            border-color: var(--accent-cyan);
            color: var(--text-white);
            background-color: rgba(171, 210, 250, 0.05);
        }

        .btn-primary {
            padding: 10px 26px;
            background: linear-gradient(135deg, var(--accent-violet), #b86be0);
            border: none;
            color: #111844;
            border-radius: 8px;
            font-size: 0.95rem;
            font-weight: 700;
            cursor: pointer;
            box-shadow: 0 4px 16px var(--glow-violet);
            transition: all 0.25s ease;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(220, 149, 255, 0.45);
        }

        @media (max-width: 768px) {
            #app-navbar {
                padding: 0 16px;
            }
            .app-nav {
                display: none;
            }
            .form-row {
                grid-template-columns: 1fr;
                gap: 0;
            }
            .form-container {
                padding: 24px;
                border-radius: 8px;
            }
        }
    </style>
</head>
<body>

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
            <span class="user-name"><%= html.escape(userName) %></span>
        </div>
    </div>
</header>

<div class="app-layout">
    <main>
        <div class="form-container">
            <a href="${pageContext.request.contextPath}/my-services" class="back-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                Back to My Services
            </a>

            <h1 class="form-title">Edit Service</h1>
            <p class="form-subtitle">Update details for your offered skill or service.</p>

            <form action="${pageContext.request.contextPath}/service-management" method="post">
                <input type="hidden" name="action" value="EDIT">
                <input type="hidden" name="serviceId" value="<%= service.getId() %>">

                <div class="form-group">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" value="<%= html.escape(service.getTitle()) %>" required>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" required><%= html.escape(service.getDescription()) %></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="category">Category</label>
                        <select id="category" name="category" required>
                            <option value="Tutoring" <%= "Tutoring".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Tutoring</option>
                            <option value="Tech & Coding" <%= "Tech & Coding".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Tech & Coding</option>
                            <option value="Creative & Design" <%= "Creative & Design".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Creative & Design</option>
                            <option value="Design" <%= "Design".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Design</option>
                            <option value="Music & Arts" <%= "Music & Arts".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Music & Arts</option>
                            <option value="Languages" <%= "Languages".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Languages</option>
                            <option value="Fitness & Sports" <%= "Fitness & Sports".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Fitness & Sports</option>
                            <option value="General Help" <%= "General Help".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>General Help</option>
                            <option value="Other" <%= "Other".equalsIgnoreCase(serviceCategory) ? "selected" : "" %>>Other</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="mode">Mode</label>
                        <select id="mode" name="mode" required>
                            <option value="Online" <%= "Online".equalsIgnoreCase(serviceMode) ? "selected" : "" %>>Online</option>
                            <option value="Offline" <%= "Offline".equalsIgnoreCase(serviceMode) ? "selected" : "" %>>Offline</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="location">Location (Optional for Online)</label>
                    <input type="text" id="location" name="location" value="<%= html.escape(service.getLocation()) %>">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="durationHours">Duration (Hours)</label>
                        <input type="number" id="durationHours" name="durationHours" min="1" value="<%= service.getDurationHours() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="creditCost">Credit Cost</label>
                        <input type="number" id="creditCost" name="creditCost" min="0" value="<%= service.getCreditCost() %>" required>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/my-services" class="btn-secondary">Cancel</a>
                    <button type="submit" class="btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </main>
</div>

</body>
</html>