<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hourglass.model.ServiceRequest" %>
<%!
    // Utility method to safely escape strings for HTML attributes
    private String escapeHtmlAttribute(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>
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
List<ServiceRequest> myRequests = (List<ServiceRequest>) request.getAttribute("myRequests");
if (myRequests == null) {
    myRequests = new java.util.ArrayList<ServiceRequest>();
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass — My Requests</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/incoming-requests.css">
    
    <!-- 
        TEMPORARY CDN DEPENDENCY FOR TESTING:
        If internet is unavailable during your college demo, download qrcode.min.js,
        place it in `src/main/webapp/js/qrcode.min.js`, and replace the src below with:
        "${pageContext.request.contextPath}/js/qrcode.min.js"
    -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>

    <style>
        /* Modal Overlay & Card Styling */
        .qr-modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100vw;
            height: 100vh;
            background: rgba(11, 19, 43, 0.85);
            backdrop-filter: blur(4px);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }

        .qr-modal-overlay.active {
            display: flex;
        }

        .qr-card {
            background: #1C2541;
            border: 1px solid #3A506B;
            border-radius: 12px;
            padding: 24px;
            width: 90%;
            max-width: 380px;
            text-align: center;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
            color: #FFFFFF;
            position: relative;
        }

        .qr-card-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: #ABD2FA;
            margin-bottom: 4px;
        }

        .qr-service-title {
            font-size: 0.95rem;
            color: #E2E8F0;
            margin-bottom: 16px;
            word-break: break-word;
        }

        .qr-canvas-container {
            background: #FFFFFF;
            padding: 16px;
            border-radius: 8px;
            display: inline-block;
            margin: 12px 0;
            min-width: 160px;
            min-height: 160px;
        }

        .qr-instructions {
            font-size: 0.85rem;
            color: #CBD5E1;
            margin: 12px 0;
            line-height: 1.4;
        }

        .qr-expiry-wrap {
            font-size: 0.9rem;
            font-weight: 600;
            color: #FFAA00;
            margin-bottom: 16px;
        }

        .qr-expired-msg {
            color: #EF4444;
            font-size: 0.85rem;
            font-weight: 600;
            display: none;
            margin-bottom: 16px;
        }

        .btn-qr-generate {
            background-color: #FFAA00;
            color: #0B132B;
            font-weight: 600;
            padding: 8px 16px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            transition: background 0.2s ease;
        }

        .btn-qr-generate:hover {
            background-color: #E69900;
        }

        .btn-modal-close {
            background: #3A506B;
            color: #FFFFFF;
            border: none;
            padding: 8px 20px;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-modal-close:hover {
            background: #4C6A8D;
        }
    </style>
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
                <a href="${pageContext.request.contextPath}/my-requests" class="nav-link active">My Requests</a>
                <a href="${pageContext.request.contextPath}/incoming-requests" class="nav-link">Incoming Requests</a>
                <a href="${pageContext.request.contextPath}/my-services" class="nav-link">My Services</a>
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
                <h1 class="page-title">My Requests</h1>
                <p class="page-subtitle">Track services you have requested from other students.</p>
            </div>
        </div>

        <div class="results-meta">
            <span class="results-count">
                <%= myRequests.size() %> request<%= myRequests.size() == 1 ? "" : "s" %> total
            </span>
        </div>

        <section class="section-container">
            <%
            if (myRequests.isEmpty()) {
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
                    <h3 class="empty-title">No requested services yet</h3>
                    <p class="empty-desc">Discover skills offered by other students to create requests.</p>
                </div>
            <%
            } else {
            %>
                <div class="requests-grid">
                <%
                for (ServiceRequest req : myRequests) {
                    String provider = req.getProviderName();
                    if (provider == null || provider.trim().isEmpty()) {
                        provider = "Student";
                    }
                    String provInitial = provider.substring(0, 1).toUpperCase();

                    String status = req.getStatus() != null ? req.getStatus().toUpperCase() : "PENDING";
                    String statusClass = "pending";

                    if ("ACCEPTED".equals(status) || "COMPLETED".equals(status) || "VERIFIED".equals(status)) {
                        statusClass = "active";
                    } else if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                        statusClass = "inactive";
                    }

                    boolean isCompleted = "COMPLETED".equals(status);
                    boolean isVerified = "VERIFIED".equals(status);
                    boolean isTransferred = req.isTransferred();

                    String statusDisplayText = status;
                    if (isVerified) {
                        statusDisplayText = isTransferred ? "VERIFIED — CREDITS TRANSFERRED" : "VERIFIED — TRANSFER PENDING";
                    }

                    int creditCost = req.getCreditCost();
                    String creditLabel = creditCost == 1 ? "credit" : "credits";
                    String rawTitle = req.getServiceTitle() != null ? req.getServiceTitle() : "Service Request";
                    String safeTitleAttr = escapeHtmlAttribute(rawTitle);
                %>
                    <article class="request-card">
                        <div class="card-header-flex">
                            <div class="user-info-group">
                                <div class="requester-avatar"><%= provInitial %></div>
                                <div class="requester-meta">
                                    <h3 class="requester-name"><%= provider %></h3>
                                    <p class="requester-sub">Service Provider</p>
                                </div>
                            </div>

                            <span class="status-badge <%= statusClass %>"><%= statusDisplayText %></span>
                        </div>

                        <div class="card-body">
                            <h4 class="service-title"><%= rawTitle %></h4>
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

                            <% if (isCompleted && !isTransferred) { %>
                                <div class="card-actions">
                                    <button type="button" 
                                            class="btn-qr-generate" 
                                            data-request-id="<%= req.getId() %>"
                                            data-service-title="<%= safeTitleAttr %>"
                                            onclick="generateQrCode(this)">
                                        Generate QR
                                    </button>
                                </div>
                            <% } else if (isVerified && !isTransferred) { %>
                                <div class="card-actions">
                                    <form action="${pageContext.request.contextPath}/verify-request" method="POST" class="action-form">
                                        <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                        <button type="submit" class="btn-action accept">Transfer Credits</button>
                                    </form>
                                </div>
                            <% } %>
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

<!-- Session Verification QR Modal -->
<div id="qrModal" class="qr-modal-overlay">
    <div class="qr-card">
        <div class="qr-card-title">Session Verification QR</div>
        <div id="qrServiceTitle" class="qr-service-title">Service Request</div>
        
        <div id="qrCanvas" class="qr-canvas-container"></div>
        
        <div id="qrInstructions" class="qr-instructions">
            Show this QR code to the service provider to verify the completed session.
        </div>

        <div id="qrExpiryWrap" class="qr-expiry-wrap">
            Expires in: <span id="qrTimer">10:00</span>
        </div>

        <div id="qrExpiredMsg" class="qr-expired-msg">
            This QR code has expired. Generate a new QR code to continue.
        </div>

        <button type="button" class="btn-modal-close" onclick="closeQrModal()">Close</button>
    </div>
</div>

<script>
    var countdownInterval = null;
    var qrCodeInstance = null;
    var contextPath = "${pageContext.request.contextPath}";

    function generateQrCode(buttonElement) {
        var requestId = buttonElement.getAttribute('data-request-id');
        var serviceTitle = buttonElement.getAttribute('data-service-title');

        if (typeof QRCode === 'undefined') {
            alert('QR generation library is unavailable. Ensure qrcode.min.js is present.');
            return;
        }

        document.getElementById('qrServiceTitle').innerText = serviceTitle;
        document.getElementById('qrCanvas').innerHTML = '';
        document.getElementById('qrInstructions').style.display = 'block';
        document.getElementById('qrExpiryWrap').style.display = 'block';
        document.getElementById('qrExpiredMsg').style.display = 'none';

        fetch(contextPath + '/generate-qr', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'requestId=' + encodeURIComponent(requestId)
        })
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            if (data.success) {
                var payload = JSON.stringify({
                    requestId: data.requestId,
                    token: data.token
                });

                qrCodeInstance = new QRCode(document.getElementById('qrCanvas'), {
                    text: payload,
                    width: 160,
                    height: 160,
                    colorDark : "#0B132B",
                    colorLight : "#FFFFFF",
                    correctLevel : QRCode.CorrectLevel.H
                });

                startTimer(data.expiresInSeconds || 600);
                document.getElementById('qrModal').classList.add('active');
            } else {
                alert(data.message || 'Failed to generate QR code.');
            }
        })
        .catch(function(error) {
            console.error('Error:', error);
            alert('An error occurred while generating the QR code.');
        });
    }

    function startTimer(duration) {
        clearInterval(countdownInterval);
        var timer = duration;

        countdownInterval = setInterval(function () {
            var minutes = parseInt(timer / 60, 10);
            var seconds = parseInt(timer % 60, 10);

            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;

            document.getElementById('qrTimer').innerText = minutes + ":" + seconds;

            if (--timer < 0) {
                clearInterval(countdownInterval);
                document.getElementById('qrCanvas').innerHTML = '';
                document.getElementById('qrInstructions').style.display = 'none';
                document.getElementById('qrExpiryWrap').style.display = 'none';
                document.getElementById('qrExpiredMsg').style.display = 'block';
            }
        }, 1000);
    }

    function closeQrModal() {
        clearInterval(countdownInterval);
        document.getElementById('qrModal').classList.remove('active');
    }
</script>

</body>
</html>