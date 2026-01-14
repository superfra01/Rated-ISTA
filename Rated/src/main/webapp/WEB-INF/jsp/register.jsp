<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*" import="model.*"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/favicon.ico">
    <link rel="stylesheet" href="static/css/loginRegister.css">
    <script src="static/scripts/registerValidation.js" defer></script>
</head>
<body>
    <jsp:include page="header.jsp" />
    
    <main>
        <div class="login-container">
            <h3>Una ricca community ti aspetta</h3>
            <h4>Register</h4>
            <form id="regForm" action="<%= request.getContextPath() %>/register" method="post" enctype="multipart/form-data">
                
                <!-- Username -->
                <input type="text" id="username" name="username" placeholder="Username" required>
                <span id="errorUsername" aria-live="polite"></span>
                
                <!-- Email -->
                <input type="text" id="email" name="email" placeholder="E-mail" required>
                <span id="errorEmail" aria-live="polite"></span>
                
                <!-- Password -->
                <input type="password" id="password" name="password" placeholder="Password" required>
                <span id="errorPassword" aria-live="polite"></span>
                
                <!-- Conferma Password -->
                <input type="password" id="confirm_password" name="confirm_password" placeholder="Confirm Password" required>
                <span id="errorConfirmPassword" aria-live="polite"></span>
                
                <!-- Caricamento immagine profilo -->
                <input type="file" id="profile_icon" name="profile_icon" accept="image/*" required>
                <span id="errorProfileIcon" aria-live="polite"></span>
                
                <!-- Biografia -->
                <textarea id="bio" name="biography" placeholder="Biografia" rows="4" required></textarea>
                <span id="errorBio" aria-live="polite"></span>
                <br>
                
                <button type="submit">Register</button>
            </form>
            <p>Hai già un account? <a href="<%= request.getContextPath() %>/login" class="login-now">Login</a></p>
        </div>
    </main>
</body>
</html>
