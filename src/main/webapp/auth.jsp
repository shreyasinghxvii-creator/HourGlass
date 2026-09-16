<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HourGlass - Authentication</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/auth.css">
</head>
<body>

    <%
        String initialMode = (String) request.getAttribute("initialMode");
        if (initialMode == null) {
            initialMode = "login";
        }
        boolean isRegister = "register".equals(initialMode);

        String errorMessage = (String) request.getAttribute("errorMessage");
        String successMessage = (String) request.getAttribute("successMessage");
        String loginEmail = (String) request.getAttribute("loginEmail");
        if (loginEmail == null) {
            loginEmail = (String) request.getAttribute("email");
        }
        String regUsername = (String) request.getAttribute("regUsername");
        String regEmail = (String) request.getAttribute("regEmail");
    %>

    <div class="auth-container <%= isRegister ? "register-mode right-panel-active" : "" %>" id="authContainer" data-initial-mode="<%= initialMode %>">
        
        <!-- Login Form Panel (Left in Login Mode) -->
        <div class="form-container login-container">
            <form action="${pageContext.request.contextPath}/login" method="POST">
                <h2>Welcome Back</h2>
                <p class="subtitle">Log in to manage your time credits</p>

                <% if (successMessage != null && !successMessage.isEmpty()) { %>
                    <div class="alert alert-success">
                        <%= successMessage %>
                    </div>
                <% } %>

                <% if (!isRegister && errorMessage != null && !errorMessage.isEmpty()) { %>
                    <div class="alert alert-danger">
                        <%= errorMessage %>
                    </div>
                <% } %>

                <div class="input-group">
                    <label for="login-email">Email</label>
                    <input type="email" id="login-email" name="email" 
                           value="<%= loginEmail != null ? loginEmail : "" %>" 
                           placeholder="Enter your email" required>
                </div>

                <div class="input-group">
                    <label for="login-password">Password</label>
                    <input type="password" id="login-password" name="password" 
                           placeholder="Enter your password" required>
                </div>

                <button type="submit" class="btn btn-primary">Log In</button>

                <p class="toggle-text">
                    Don't have an account? <a href="#" id="to-register">Register</a>
                </p>
            </form>
        </div>

        <!-- Register Form Panel (Right in Register Mode) -->
        <div class="form-container register-container">
            <form action="${pageContext.request.contextPath}/register" method="POST">
                <h2>Create Account</h2>
                <p class="subtitle">Join HourGlass to trade skills and time</p>

                <% if (isRegister && errorMessage != null && !errorMessage.isEmpty()) { %>
                    <div class="alert alert-danger">
                        <%= errorMessage %>
                    </div>
                <% } %>

                <div class="input-group">
                    <label for="reg-username">Username</label>
                    <input type="text" id="reg-username" name="username" 
                           value="<%= regUsername != null ? regUsername : "" %>" 
                           placeholder="Choose a username" required>
                </div>

                <div class="input-group">
                    <label for="reg-email">Email</label>
                    <input type="email" id="reg-email" name="email" 
                           value="<%= regEmail != null ? regEmail : "" %>" 
                           placeholder="Enter your email" required>
                </div>

                <div class="input-group">
                    <label for="reg-password">Password</label>
                    <input type="password" id="reg-password" name="password" 
                           placeholder="Create a password" required>
                </div>

                <div class="input-group">
                    <label for="reg-confirmPassword">Confirm Password</label>
                    <input type="password" id="reg-confirmPassword" name="confirmPassword" 
                           placeholder="Confirm your password" required>
                </div>

                <button type="submit" class="btn btn-primary">Register</button>

                <p class="toggle-text">
                    Already have an account? <a href="#" id="to-login">Log In</a>
                </p>
            </form>
        </div>

        <!-- Shared HourGlass Visual Container -->
        <div class="overlay-container">
            <div class="overlay">
                <div class="overlay-panel overlay-left">
                    <div class="hourglass-visual">
                        <div class="hourglass-3d" aria-hidden="true">
                            <div class="hourglass-frame">
                                <div class="hourglass-top"></div>

                                <div class="hourglass-glass">
                                    <div class="glass-highlight"></div>

                                    <div class="sand-chamber sand-top">
                                        <div class="sand"></div>
                                    </div>

                                    <div class="sand-stream"></div>

                                    <div class="sand-chamber sand-bottom">
                                        <div class="sand"></div>
                                    </div>
                                </div>

                                <div class="hourglass-bottom"></div>
                            </div>
                        </div>
                        <h1>HourGlass</h1>
                        <p>Exchange skills. Share time. Connect with your community.</p>
                    </div>
                </div>
                <div class="overlay-panel overlay-right">
                    <div class="hourglass-visual">
                        <div class="hourglass-3d" aria-hidden="true">
                            <div class="hourglass-frame">
                                <div class="hourglass-top"></div>

                                <div class="hourglass-glass">
                                    <div class="glass-highlight"></div>

                                    <div class="sand-chamber sand-top">
                                        <div class="sand"></div>
                                    </div>

                                    <div class="sand-stream"></div>

                                    <div class="sand-chamber sand-bottom">
                                        <div class="sand"></div>
                                    </div>
                                </div>

                                <div class="hourglass-bottom"></div>
                            </div>
                        </div>
                        <h1>HourGlass</h1>
                        <p>Exchange skills. Share time. Connect with your community.</p>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <script src="${pageContext.request.contextPath}/auth.js"></script>
</body>
</html>