<%-- 
    Document   : admin
    Created on : May 25, 2025, 12:58:45 PM
    Author     : DELL
--%>

<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
    User acc = (User) session.getAttribute("user");
    if (acc == null) {
        response.sendRedirect("login");
        return;
    }
%>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Quản Trị F-SKILL</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon_io/favicon.ico">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css"/>
    <style>
        /* CSS for the entire page, only applied to the welcome card for a consistent look */
        .welcome-card-wrapper {
            flex: 1; /* Allows this wrapper to take up the remaining space */
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 2rem; /* Add some padding for spacing */
        }

        /* Card containing the content */
        .container {
            background-color: #e8f4ff;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
            max-width: 90%;
            width: 600px;
            position: relative;
            z-index: 1;
            transition: transform 0.5s ease-in-out;
            cursor: pointer;
        }

        .container:hover {
            transform: translateY(-10px);
        }

        /* Container for the image */
        .image-container {
            width: 150px;
            height: 150px;
            margin: 0 auto 30px;
            border-radius: 50%;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            transition: transform 0.3s ease-in-out;
        }

        .image-container:hover {
            transform: scale(1.1);
        }

        .profile-pic {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border: 5px solid #4a90e2;
        }

        /* Main title */
        .welcome-text {
            font-family: 'Poppins', sans-serif;
            font-size: 3.5rem;
            font-weight: 700;
            color: #4a90e2;
            margin: 0;
            background: linear-gradient(90deg, #4a90e2 0%, #7b4397 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
            transition: letter-spacing 0.3s ease-in-out;
        }

        .welcome-text:hover {
            letter-spacing: 2px;
        }

        /* Subtitle */
        .subtitle {
            font-size: 1.2rem;
            color: #666;
            margin-top: 10px;
        }

        /* Call-to-action button */
        .cta-button {
            margin-top: 30px;
            padding: 12px 25px;
            background: #4a90e2;
            color: #fff;
            border: none;
            border-radius: 30px;
            font-size: 1rem;
            font-weight: bold;
            cursor: pointer;
            box-shadow: 0 4px 15px rgba(74, 144, 226, 0.4);
            transition: all 0.3s ease-in-out;
        }

        .cta-button:hover {
            background: #7b4397;
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(123, 67, 151, 0.5);
        }

        /* Responsive Design for smaller screens */
        @media (max-width: 600px) {
            .welcome-text {
                font-size: 2.5rem;
            }

            .container {
                padding: 30px;
            }

            .subtitle {
                font-size: 1rem;
            }
        }
    </style>
</head>
<body class="flex flex-col h-screen">
<jsp:include page="/layout/header.jsp" />

<div class="flex flex-grow">
    <jsp:include page="/layout/sidebar_admin.jsp" />

    <!-- Main content area -->
    <main class="welcome-card-wrapper">
        <div class="container">
            <div class="image-container">
                <c:choose>
                    <c:when test="${not empty sessionScope.user.avatar}">
                        <img src="${sessionScope.user.imageDataURI}" alt="Admin Avatar" class="profile-pic">
                    </c:when>
                    <c:otherwise>
                        <img src="https://placehold.co/150x150/cccccc/333333?text=Admin" alt="Default Admin Avatar" class="profile-pic">
                    </c:otherwise>
                </c:choose>
            </div>
            <h1 class="welcome-text">Welcome Back, Admin!</h1>
            <p class="subtitle">We're glad to see you again. Let's get to work!</p>

        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
