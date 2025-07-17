
<%-- 
    Document   : notification
    Created on : 15 Jul. 2025, 2:49:40 pm
    Author     : Hua Khanh Duy - CE180230 - SE1814
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<div class="relative z-50" id="notification-bell-container">

    <c:set var="unreadCount" value="0" />
    <c:forEach var="noti" items="${sessionScope.listNotification}">
        <c:if test="${not noti.status}">
            <c:set var="unreadCount" value="${unreadCount + 1}" />
        </c:if>
    </c:forEach>

    <button id="notification-bell" class="relative focus:outline-none">
        <i class="fas fa-bell text-gray-700 text-xl"></i>
        <c:if test="${unreadCount > 0}">
            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">
                ${unreadCount}
            </span>
        </c:if>
    </button>


    <div id="notification-popup" class="hidden absolute right-0 mt-3 w-[380px] bg-white border border-gray-200 rounded-xl shadow-xl z-50">

        <div class="flex justify-between items-center px-4 py-3 border-b">
            <h3 class="font-semibold text-gray-800 text-base">Notification</h3>

        </div>


        <div id="notification-list" class="max-h-[400px] overflow-y-auto divide-y divide-gray-100">
            <c:forEach var="noti" items="${sessionScope.listNotification}">
                <div class="notification-item" id="noti-${noti.notificationId}">

                    <div class="flex items-start gap-3 px-4 py-3 hover:bg-gray-100 cursor-pointer notification-item
                         <c:if test='${noti.status}'>opacity-50 text-gray-400</c:if>'
                         id="noti-${noti.notificationId}"
                         data-id="${noti.notificationId}"
                         onclick="markAsRead('${noti.notificationId}')">


                        <img src="${noti.userName.imageDataURI}" class="w-10 h-10 rounded-full object-cover flex-shrink-0" alt="avatar" />


                        <div class="flex-1 text-sm">
                            <p>
                                <span class="font-semibold">${noti.userName.userName}</span>
                                ${noti.notificationMessage}
                            </p>
                            <p class="text-xs text-gray-500 mt-1">
                                <fmt:formatDate value="${noti.notificationDate}" pattern="HH:mm dd/MM/yyyy" />
                            </p>
                        </div>

                        <c:if test="${not noti.status}">
                            <div class="mt-2 w-2 h-2 bg-blue-500 rounded-full"></div>
                        </c:if>
                    </div>


                    <input type="hidden" id="statusField${noti.notificationId}" name="status" value="${noti.status}" />
                </div>
            </c:forEach>
        </div>


        <div class="text-center border-t py-3">
            <button class="text-sm text-gray-600 hover:bg-gray-200 px-4 py-1 rounded-md">
                Xem thông báo trước đó
            </button>
        </div>
    </div>
</div>
<script>
    function markAsRead(id) {
        // Gửi request cập nhật server
        fetch('notification', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'id=' + encodeURIComponent(id)
        }).then(response => {
            if (response.ok) {
                // Cập nhật UI sau khi server xác nhận đã lưu
                const input = document.getElementById("statusField" + id);
                const item = document.getElementById("noti-" + id);

                if (input)
                    input.value = true;
                if (item) {
                    item.classList.add("opacity-50", "text-gray-400");
                    const dot = item.querySelector(".bg-blue-500");
                    if (dot)
                        dot.remove();
                }
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        const notificationBell = document.getElementById('notification-bell');
        const notificationPopup = document.getElementById('notification-popup');

        notificationBell.addEventListener('click', function (event) {
            event.stopPropagation();
            notificationPopup.classList.toggle('hidden');
        });

        // Close popup when clicking outside
        document.addEventListener('click', function (event) {
            if (!notificationBell.contains(event.target) && !notificationPopup.contains(event.target)) {
                notificationPopup.classList.add('hidden');
            }
        });
    });

    document.addEventListener('DOMContentLoaded', function () {
        const notificationBell = document.getElementById('notification-bell');
        const notificationPopup = document.getElementById('notification-popup');

        notificationBell.addEventListener('click', function(event) {
            event.stopPropagation();
            notificationPopup.classList.toggle('hidden');
        });

        // Close popup when clicking outside
        document.addEventListener('click', function (event) {
            if (!notificationBell.contains(event.target) && !notificationPopup.contains(event.target)) {
                notificationPopup.classList.add('hidden');
            }
        });
    });
</script>
