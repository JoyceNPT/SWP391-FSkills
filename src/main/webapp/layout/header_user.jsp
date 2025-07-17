<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/transition.css">
<script src="${pageContext.request.contextPath}/js/transition.js"></script>
<header class="header bg-white shadow-md p-4 rounded-b-lg">
    <div class="flex items-center justify-between">
        <%--        <div class="w-1/4">--%>
        <%--            --%>
        <%--        </div>--%>

        <div class="flex-grow flex justify-center">
            <div class="relative w-full max-w-xl">
                <input type="text" placeholder="Tìm kiếm..." class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500">
                <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
        </div>

        <div class="flex items-center space-x-4 w-1/4 justify-end">
            <jsp:include page="/layout/notification.jsp"/>
            <%-- Phần user profile/avatar --%>
            <div class="flex items-center space-x-2">
                <div class="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center text-gray-500 text-sm font-semibold">
                    <%-- Display user avatar if available --%>
                    <c:choose>
                        <c:when test="${not empty sessionScope.user.avatar}">
                            <img src="${sessionScope.user.imageDataURI}" alt="User Avatar" class="w-8 h-8 rounded-full">
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-user"></i>
                        </c:otherwise>
                    </c:choose>
                </div>
                <%-- Display user name from session --%>
                <span class="font-medium text-gray-700">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user.displayName}">
                            ${sessionScope.user.displayName}
                        </c:when>
                        <c:otherwise>
                            Instructor
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </div>
</header>

