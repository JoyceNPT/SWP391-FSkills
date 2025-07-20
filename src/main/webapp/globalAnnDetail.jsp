<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${announcement.title}</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon_io/favicon.ico">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f0f2f5;
            word-break: break-word;
            padding-top: 60px;
            padding-left: 80px;
        }

        .container-detail {
            max-width: 900px;
            margin: 20px auto; 
            background-color: #fff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,.08);
            overflow-wrap: anywhere;
            position: unset;
            z-index: unset;
        }

        .announcement-header {
            border-bottom: 1px solid #eee;
            padding-bottom: 15px;
            margin-bottom: 20px;
            position: unset;
            z-index: unset;
        }

        .announcement-header h1 {
            margin: 0;
            color: #333;
            font-size: 2.2em;
            font-weight: bold;
            word-break: break-word;
            overflow-wrap: break-word;
            margin-top: 5px; 
        }

        .announcement-meta {
            font-size: 0.9em;
            color: #777;
            margin-top: 5px;
            display: flex;
            align-items: center;
            flex-wrap: wrap;
        }

        .announcement-meta span {
            margin-right: 15px;
        }

        .announcement-meta .icon-text {
            display: inline-flex;
            align-items: center;
            margin-right: 15px;
        }

        .announcement-meta .icon-text i {
            margin-right: 5px;
        }

        .announcement-image-detail {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 20px auto;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            object-fit: contain;
        }

        .no-image-detail-placeholder {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 300px;
            background-color: #e2e8f0;
            color: #4a5568;
            font-size: 1.5rem;
            text-align: center;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin: 20px auto;
        }

        .announcement-content-detail {
            line-height: 1.8;
            color: #333;
            word-break: break-word;
            overflow-wrap: anywhere;
        }

        .announcement-footer {
            border-top: 1px solid #eee;
            padding-top: 15px;
            margin-top: 30px;
            text-align: right;
        }

        .announcement-type-detail {
            background-color: #ef4444;
            color: white;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 0.85em;
            margin-right: 10px;
            display: inline-block;
            font-weight: bold;
            margin-bottom: 8px; 
        }
    </style>
</head>
<body>

<div class="container-detail">
    <jsp:include page="/layout/header.jsp" />
    <jsp:include page="/layout/sidebar_user.jsp" />
    <c:if test="${empty announcement}">
        <p class="text-center text-gray-600 text-xl py-10">No announcement.</p>
    </c:if>

    <c:if test="${not empty announcement}">
        <div class="announcement-header">
            <span class="announcement-type-detail">Announcement</span>
            <h1>${announcement.title}</h1>
            <div class="announcement-meta">
                <span>Posted by: ${announcement.userId.displayName != null ? announcement.userId.displayName : announcement.userId.userName}</span>
                <span>- <fmt:formatDate value="${announcement.createDate}" pattern="dd/MM/yyyy HH:mm"/></span>
            </div>
        </div>

        <c:if test="${announcement.announcementImage != null}">
            <img src="${announcement.imageDataURI}" alt="${announcement.title}" class="announcement-image-detail">
        </c:if>
        <c:if test="${announcement.announcementImage == null}">
            <div class="no-image-detail-placeholder">No Image Available</div>
        </c:if>

        <div class="announcement-content-detail">
            <p class="text-gray-800">
                <c:out value="${announcement.announcementText}" escapeXml="false"/>
            </p>
        </div>
    </c:if>
</div>
</body>
</html>