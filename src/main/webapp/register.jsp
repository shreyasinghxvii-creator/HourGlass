<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register | HourGlass</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: #f7f6f2;
            color: #1a1a1a;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .card {
            background: #ffffff;
            padding: 2.5rem;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            width: 100%;
            max-width: 400px;
            border: 1px solid #e6e4dc;
        }
        h2 {
            margin-top: 0;
            font-weight: 600;
            color: #1a1a1a;
            margin-bottom: 0.5rem;
        }
        .subtitle {
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 1.5rem;
        }
        .form-group {
            margin-bottom: 1.25rem;
        }
        label {
            display: block;
            font-size: 0.85rem;
            font-weight: 500;
            margin-bottom: 0.5rem;
        }
        input[type="text"], input[type="email"], input[type="password"] {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 0.95rem;
        }
        input:focus {
            outline: none;
            border-color: #c68a3b;
        }
        button {
            width: 100%;
            padding: 0.75rem;
            background-color: #1a1a1a;
            color: #ffffff;
            border: none;
            border-radius: 4px;
            font-weight: 500;
            font-size: 0.95rem;
            cursor: pointer;
            margin-top: 0.5rem;
        }
        button:hover {
            background-color: #333;
        }
        .error {
            background-color: #fdf3f2;
            color: #b33939;
            padding: 0.75rem;
            border-radius: 4px;
            font-size: 0.85rem;
            margin-bottom: 1rem;
            border: 1px solid #f5c6cb;
        }
        .footer-link {
            text-align: center;
            margin-top: 1.25rem;
            font-size: 0.85rem;
            color: #666;
        }
        .footer-link a {
            color: #c68a3b;
            text-decoration: none;
            font-weight: 500;
        }
        .footer-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="card">
    <h2>Join HourGlass</h2>
    <div class="subtitle">A campus where skills circulate. 1 hour = 1 credit.</div>

    <% 
        String error = request.getParameter("error");
        if ("failed".equals(error)) {
    %>
        <div class="error">Registration failed. Email might already be registered.</div>
    <% } %>

    <form action="register" method="POST">
        <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" name="name" required placeholder="Rahul Sharma">
        </div>
        
        <div class="form-group">
            <label for="email">Campus Email</label>
            <input type="email" id="email" name="email" required placeholder="student@campus.edu">
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required placeholder="••••••••">
        </div>

        <button type="submit">Create Account</button>
    </form>

    <div class="footer-link">
        Already have an account? <a href="login.jsp">Log in</a>
    </div>
</div>

</body>
</html>