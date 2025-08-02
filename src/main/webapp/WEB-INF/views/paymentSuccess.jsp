<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Payment Successful</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous">
    <link rel="stylesheet" href="/FSkills/css/sidebar.css">
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
        }

        .h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--primary-color);
            text-align: center;
            margin: 2rem 0 1.5rem;
        }

        .success-container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            border-radius: var(--border-radius);
            box-shadow: var(--card-shadow);
            padding: 2rem;
            text-align: center;
        }

        .success-icon {
            font-size: 4rem;
            color: var(--success-color);
            margin-bottom: 1rem;
        }

        .table-container {
            background: white;
            border-radius: var(--border-radius);
            box-shadow: var(--card-shadow);
            margin: 1rem auto;
            max-width: 600px;
        }

        .table th, .table td {
            vertical-align: middle;
            padding: 0.75rem;
            color: var(--primary-color);
        }

        .btn-primary {
            background-color: var(--secondary-color);
            border-color: var(--secondary-color);
            padding: 0.75rem 1.5rem;
            font-weight: 600;
            transition: var(--transition);
            margin: 0.5rem;
        }

        .btn-primary:hover {
            background-color: #2980b9;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
            padding: 0.75rem 1.5rem;
            font-weight: 600;
            transition: var(--transition);
            margin: 0.5rem;
        }

        .btn-secondary:hover {
            background-color: #1a252f;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <c:if test="${empty sessionScope.user}">
        <c:redirect url="/login"/>
    </c:if>
    <%@include file="../../layout/sidebar_user.jsp" %>
    <main class="main">
        <p class="h1">Payment Successful</p>
        <div class="success-container">
            <i class="fas fa-check-circle success-icon"></i>
            <h2>Thank You, <c:out value="${user.userName}"/>!</h2>
            <p>Your payment has been successfully processed. You can now access your purchased courses.</p>
            
            <h4>Payment Details</h4>
            <div class="table-container">
                <table class="table table-bordered">
                    <tr>
                        <th>Payment Date</th>
                        <td><fmt:formatDate value="${paymentDate}" pattern="dd-MM-yyyy HH:mm:ss"/></td>
                    </tr>
                    <tr>
                        <th>Total Paid</th>
                        <td><fmt:formatNumber value="${finalPrice}" type="number" groupingUsed="true"/> VND</td>
                    </tr>
                    <tr>
                        <th>Voucher Used</th>
                        <td><c:out value="${voucherCode eq '0' ? 'None' : voucherCode}"/></td>
                    </tr>
                </table>
            </div>

            <c:if test="${not empty selectedCourses}">
                <h4>Purchased Courses</h4>
                <div class="table-container">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Course Name</th>
                                <th>Price</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="course" items="${selectedCourses}">
                                <tr>
                                    <td><c:out value="${course.courseName}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${course.isSale eq 0}">
                                                <fmt:formatNumber value="${course.originalPrice}" type="number" groupingUsed="true"/> VND
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber value="${course.salePrice}" type="number" groupingUsed="true"/> VND
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/cart" class="btn btn-primary">Back to Cart</a>
                <a href="${pageContext.request.contextPath}/learner/courselist" class="btn btn-primary">My Courses</a>
                <a href="${pageContext.request.contextPath}/homePage_Guest.jsp" class="btn btn-secondary">Home</a>
            </div>
        </div>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
</body>
</html>