<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/transition.css">
<script src="${pageContext.request.contextPath}/js/transition.js"></script>

<!-- Header Section -->
<header style="background-color: white; box-shadow: 0 2px 4px rgba(0,0,0,0.05); padding: 1rem 1.5rem; position: fixed !important; top: 0 !important; left: 80px; right: 0; width: calc(100% - 96px) !important; z-index: 1001 !important; border-radius: 12px; margin: 8px; transition: left 0.3s ease, width 0.3s ease;">
    <div style="max-width: 100%; margin: 0 auto; display: flex; justify-content: flex-end; align-items: center; border-radius: 8px;" class="header-container">
        <div style="white-space: nowrap; overflow: hidden; position: relative; width: 100%;" class="header-text">
            <div style="display: inline-block; padding-left: 100%; animation: scroll 10s linear infinite;" class="scrolling-text">
                <p style="margin: 0; font-weight: 600; color: #111; text-shadow: 1px 1px 2px rgba(0,0,0,0.1);">
                    Hi <span style="color: #2563eb; font-weight: 700; font-size: 0.875rem; text-shadow: 0 1px 1px rgba(0,0,0,0.1);" class="user-name">
                        <c:choose>
                            <c:when test="${not empty sessionScope.user.displayName}">
                                ${sessionScope.user.displayName}
                            </c:when>
                            <c:when test="${not empty sessionScope.adminUser.displayName}">
                                ${sessionScope.adminUser.displayName}
                            </c:when>
                            <c:otherwise>
                                Guest
                            </c:otherwise>
                        </c:choose>
                    </span> - <span style="color: #16a34a; font-weight: 500">Welcome to FSkills</span>
                </p>
            </div>
        </div>
    </div>
</header>

<style>
    @keyframes scroll {
        0% { transform: translateX(-100%); }
        100% { transform: translateX(0); }
    }

    @media (min-width: 640px) {
        .user-name {
            font-size: 1rem !important;
        }
        .scrolling-text p {
            font-size: 1rem;
        }
    }

    /* Add padding to body to prevent content from appearing under fixed header */
    body {
        padding-top: 60px !important; /* Reduced padding to minimize gap below header */
    }

    /* Handle header position when sidebar expands */
    .sidebar:hover ~ header, 
    .sidebar:hover + div header,
    .sidebar:hover + header {
        left: 250px !important;
        width: calc(100% - 266px) !important; /* Adjust width to account for sidebar width (250px) and margins */
    }

    /* Responsive adjustments for smaller screens */
    @media (max-width: 768px) {
        header {
            left: 0 !important; /* Start with no left margin on small screens */
            width: calc(100% - 16px) !important; /* Full width minus margins */
        }

        .sidebar:hover ~ header, 
        .sidebar:hover + div header,
        .sidebar:hover + header {
            left: 250px !important; /* When sidebar is expanded */
            width: calc(100% - 266px) !important; /* Adjust width to account for sidebar width and margins */
        }
    }
</style>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Any JavaScript functionality can be added here if needed
    });
</script>


