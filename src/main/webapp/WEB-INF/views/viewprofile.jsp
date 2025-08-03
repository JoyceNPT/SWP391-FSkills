<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Profile - F-Skills</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon_io/favicon.ico">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    <style>
        :root {
            --primary-color: #2c3e50;
            --secondary-color: #3498db;
            --success-color: #2ecc71;
            --background-color: #f8f9fa;
            --card-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            --border-radius: 8px;
            --transition: all 0.3s ease;
        }

        .main {
            min-height: 100vh;
            padding: 2rem 3vw;
            background-color: var(--background-color);
            margin-top: 60px;
        }

        .h1 {
            font-size: 3rem;
            font-weight: 700;
            color: var(--primary-color);
            text-align: center;
            margin: 2rem 0 1.5rem;
        }

        .profile-card {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            border-radius: var(--border-radius);
            box-shadow: var(--card-shadow);
            padding: 2.5rem;
        }

        .profile-header {
            display: flex;
            align-items: center;
            gap: 2rem;
            margin-bottom: 2.5rem;
        }

        .avatar img {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid var(--primary-color);
        }

        .user-info h2 {
            font-size: 2.2rem;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 0.75rem;
        }

        .user-info p {
            font-size: 1.2rem;
            color: #666;
            margin: 0;
        }

        .verified-badge {
            display: inline-flex;
            align-items: center;
            color: var(--success-color);
            font-size: 1rem;
            margin-left: 10px;
            background-color: rgba(46, 204, 113, 0.1);
            padding: 3px 10px;
            border-radius: 12px;
            font-weight: 500;
        }

        .verified-badge i {
            margin-right: 5px;
        }

        .unverified-badge {
            display: inline-flex;
            align-items: center;
            color: #dc3545;
            font-size: 1rem;
            margin-left: 10px;
            background-color: rgba(220, 53, 69, 0.1);
            padding: 3px 10px;
            border-radius: 12px;
            font-weight: 500;
        }

        .unverified-badge i {
            margin-right: 5px;
        }

        .profile-details {
            display: grid;
            gap: 1.5rem;
            font-size: 1.1rem;
            color: #333;
        }

        .field {
            display: flex;
            flex-direction: column;
        }

        .field-label {
            font-size: 1rem;
            font-weight: 500;
            color: var(--primary-color);
            margin-bottom: 0.4rem;
        }

        .field-value {
            font-size: 1.1rem;
            color: #333;
        }

        .course-list, .degree-list {
            margin-top: 2.5rem;
            padding: 1.5rem;
            background-color: white;
            border-radius: var(--border-radius);
            box-shadow: var(--card-shadow);
        }

        .course-list h3, .degree-list h3 {
            font-size: 1.8rem;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 1.5rem;
        }

        .course-grid, .degree-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 1.5rem;
        }

        .course-item, .degree-item {
            background-color: #f8f9fa;
            border-radius: var(--border-radius);
            overflow: hidden;
            text-align: center;
            transition: var(--transition);
        }

        .course-item:hover, .degree-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
        }

        .course-item img, .degree-item img {
            width: 100%;
            height: 180px;
            object-fit: cover;
        }

        .course-item a, .degree-item a {
            display: block;
            padding: 0.75rem;
            font-size: 1rem;
            font-weight: 500;
            color: var(--secondary-color);
            text-decoration: none;
        }

        .course-item a:hover, .degree-item a:hover {
            color: #1e40af;
        }

        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 1.5rem;
            gap: 0.5rem;
        }

        .pagination button {
            padding: 0.5rem 1rem;
            font-size: 1rem;
            border: 1px solid var(--secondary-color);
            background-color: white;
            color: var(--secondary-color);
            border-radius: 4px;
            cursor: pointer;
            transition: var(--transition);
        }

        .pagination button:hover {
            background-color: var(--secondary-color);
            color: white;
        }

        .pagination button:disabled {
            border-color: #ccc;
            color: #ccc;
            cursor: not-allowed;
        }

        .error-message {
            max-width: 800px;
            margin: 2rem auto;
        }
    </style>
</head>
<body>
    <c:if test="${empty sessionScope.user}">
        <c:redirect url="/login"/>
    </c:if>
    <c:choose>
        <c:when test="${sessionScope.user.role eq 'ADMIN'}">
            <%@include file="/layout/sidebar_admin.jsp" %>
        </c:when>
        <c:otherwise>
            <%@include file="/layout/sidebar_user.jsp" %>
        </c:otherwise>
    </c:choose>
    <%@include file="/layout/header.jsp" %>

    <main class="main">
        <p class="h1">User Profile</p>
        <c:choose>
            <c:when test="${not empty errorMessage}">
                <div class="alert alert-danger error-message" role="alert">
                    ${errorMessage}
                </div>
            </c:when>
            <c:when test="${not empty profile}">
                <div class="profile-card">
                    <div class="profile-header">
                        <div class="avatar">
                            <c:choose>
                                <c:when test="${not empty profile.imageDataURI}">
                                    <img src="${profile.imageDataURI}" alt="Avatar"
                                         onerror="this.src='${pageContext.request.contextPath}/images/default-avatar.png'">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/default-avatar.png" alt="Default Avatar">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="user-info">
                            <h2><c:out value="${not empty profile.displayName ? profile.displayName : profile.userName}"/></h2>
                            <c:if test="${not empty profile.email}">
                                <p>
                                    <c:out value="${profile.email}"/>
                                    <c:choose>
                                        <c:when test="${profile.isVerified}">
                                            <span class="verified-badge" title="Confirmed">
                                                <i class="fas fa-check-circle"></i> Confirmed
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="unverified-badge" title="Not Verified">
                                                <i class="fas fa-exclamation-circle"></i> Not Verified
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </c:if>
                        </div>
                    </div>
                    <div class="profile-details">
                        <c:if test="${not empty profile.phone}">
                            <div class="field phone-field">
                                <span class="field-label">Phone Number</span>
                                <span class="field-value"><c:out value="${profile.phone}"/></span>
                            </div>
                        </c:if>
                        <c:if test="${profile.gender == 1 || profile.gender == 0}">
                            <div class="field gender-field">
                                <span class="field-label">Gender</span>
                                <span class="field-value">${profile.gender == 1 ? 'Male' : 'Female'}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty profile.dateOfBirth}">
                            <div class="field dob-field">
                                <span class="field-label">Date of Birth</span>
                                <span class="field-value"><fmt:formatDate value="${profile.dateOfBirth}" pattern="dd-MM-yyyy" /></span>
                            </div>
                        </c:if>
                        <c:if test="${not empty profile.info}">
                            <div class="field address-field">
                                <span class="field-label">Info</span>
                                <span class="field-value"><c:out value="${profile.info}"/></span>
                            </div>
                        </c:if>
                    </div>

                    <c:if test="${profile.role eq 'INSTRUCTOR' && not empty degreeList}">
                        <div class="degree-list">
                            <h3>Degrees & Certifications</h3>
                            <div class="degree-grid" id="degreeGrid">
                                <c:forEach var="degree" items="${degreeList}">
                                    <div class="degree-item">
                                        <a href="${fn:escapeXml(degree.link)}" target="_blank">
                                            <img src="${degree.imageDataURI}" alt="Degree Certificate"
                                                 onerror="this.src='https://placehold.co/180x180/38bdf8/ffffff?text=Degree'">
                                            <span>Degree ${degree.degreeId}</span>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="pagination">
                                <button id="prevDegreePage" disabled>Previous</button>
                                <button id="nextDegreePage">Next</button>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${profile.role eq 'INSTRUCTOR' && not empty courses}">
                        <div class="course-list">
                            <h3>Courses Created</h3>
                            <div class="course-grid" id="courseGrid">
                                <c:forEach var="course" items="${courses}">
                                    <div class="course-item">
                                        <img src="${course.imageDataURI}" alt="${course.courseName}"
                                             onerror="this.src='https://placehold.co/180x180/38bdf8/ffffff?text=Course'">
                                        <a href="${pageContext.request.contextPath}/courseDetail?id=${course.courseID}">
                                            <c:out value="${course.courseName}"/>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="pagination">
                                <button id="prevCoursePage" disabled>Previous</button>
                                <button id="nextCoursePage">Next</button>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${profile.role eq 'LEARNER' && not empty completedCourses}">
                        <div class="course-list">
                            <h3>Completed Courses</h3>
                            <div class="course-grid" id="completedCourseGrid">
                                <c:forEach var="course" items="${completedCourses}">
                                    <div class="course-item">
                                        <img src="${course.imageDataURI}" alt="${course.courseName}"
                                             onerror="this.src='https://placehold.co/180x180/38bdf8/ffffff?text=Course'">
                                        <a href="${pageContext.request.contextPath}/courseDetail?id=${course.courseID}">
                                            <c:out value="${course.courseName}"/>
                                        </a>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="pagination">
                                <button id="prevCompletedCoursePage" disabled>Previous</button>
                                <button id="nextCompletedCoursePage">Next</button>
                            </div>
                        </div>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-danger error-message" role="alert">
                    Profile not found.
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            // Pagination for degrees
            const degreeItems = document.querySelectorAll('#degreeGrid .degree-item');
            const prevDegreeBtn = document.getElementById('prevDegreePage');
            const nextDegreeBtn = document.getElementById('nextDegreePage');
            const itemsPerPage = 6;
            let currentDegreePage = 1;

            function showDegreePage(page) {
                const start = (page - 1) * itemsPerPage;
                const end = start + itemsPerPage;

                degreeItems.forEach((item, index) => {
                    item.style.display = (index >= start && index < end) ? 'block' : 'none';
                });

                prevDegreeBtn.disabled = page === 1;
                nextDegreeBtn.disabled = end >= degreeItems.length;
            }

            if (degreeItems.length > 0) {
                showDegreePage(currentDegreePage);
                prevDegreeBtn.addEventListener('click', () => {
                    if (currentDegreePage > 1) {
                        currentDegreePage--;
                        showDegreePage(currentDegreePage);
                    }
                });
                nextDegreeBtn.addEventListener('click', () => {
                    if (currentDegreePage * itemsPerPage < degreeItems.length) {
                        currentDegreePage++;
                        showDegreePage(currentDegreePage);
                    }
                });
            }

            // Pagination for instructor courses
            const courseItems = document.querySelectorAll('#courseGrid .course-item');
            const prevCourseBtn = document.getElementById('prevCoursePage');
            const nextCourseBtn = document.getElementById('nextCoursePage');
            let currentCoursePage = 1;

            function showCoursePage(page) {
                const start = (page - 1) * itemsPerPage;
                const end = start + itemsPerPage;

                courseItems.forEach((item, index) => {
                    item.style.display = (index >= start && index < end) ? 'block' : 'none';
                });

                prevCourseBtn.disabled = page === 1;
                nextCourseBtn.disabled = end >= courseItems.length;
            }

            if (courseItems.length > 0) {
                showCoursePage(currentCoursePage);
                prevCourseBtn.addEventListener('click', () => {
                    if (currentCoursePage > 1) {
                        currentCoursePage--;
                        showCoursePage(currentCoursePage);
                    }
                });
                nextCourseBtn.addEventListener('click', () => {
                    if (currentCoursePage * itemsPerPage < courseItems.length) {
                        currentCoursePage++;
                        showCoursePage(currentCoursePage);
                    }
                });
            }

            // Pagination for completed courses
            const completedCourseItems = document.querySelectorAll('#completedCourseGrid .course-item');
            const prevCompletedCourseBtn = document.getElementById('prevCompletedCoursePage');
            const nextCompletedCourseBtn = document.getElementById('nextCompletedCoursePage');
            let currentCompletedCoursePage = 1;

            function showCompletedCoursePage(page) {
                const start = (page - 1) * itemsPerPage;
                const end = start + itemsPerPage;

                completedCourseItems.forEach((item, index) => {
                    item.style.display = (index >= start && index < end) ? 'block' : 'none';
                });

                prevCompletedCourseBtn.disabled = page === 1;
                nextCompletedCourseBtn.disabled = end >= completedCourseItems.length;
            }

            if (completedCourseItems.length > 0) {
                showCompletedCoursePage(currentCompletedCoursePage);
                prevCompletedCourseBtn.addEventListener('click', () => {
                    if (currentCompletedCoursePage > 1) {
                        currentCompletedCoursePage--;
                        showCompletedCoursePage(currentCompletedCoursePage);
                    }
                });
                nextCompletedCourseBtn.addEventListener('click', () => {
                    if (currentCompletedCoursePage * itemsPerPage < completedCourseItems.length) {
                        currentCompletedCoursePage++;
                        showCompletedCoursePage(currentCompletedCoursePage);
                    }
                });
            }
        });
    </script>

    <%@include file="/layout/footer.jsp" %>
</body>
</html>