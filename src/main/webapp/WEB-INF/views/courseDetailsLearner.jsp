<%@page import="model.Notification"%>
<%@page import="java.util.List"%>
<%@page import="model.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    User acc = (User) session.getAttribute("user");
    List<Notification> listNotification = (List<Notification>) request.getAttribute("listNotification");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${course.courseName}</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            body {
                font-family: 'Inter', sans-serif;
            }

            .sidebar-container {
                width: 100px;
                transition: width 0.3s ease-in-out;
                overflow-x: hidden;
                position: fixed;
                top: 0;
                left: 0;
                height: 100vh;
                z-index: 1000;
            }

            .sidebar-container:hover {
                width: 250px;
            }

            .sidebar-container .sidebar-logo span,
            .sidebar-container .nav-link span,
            .sidebar-container .user-info span {
                opacity: 0;
                visibility: hidden;
                white-space: nowrap;
                transition: opacity 0.3s ease-in-out, visibility 0.3s ease-in-out;
            }

            .sidebar-container:hover .sidebar-logo span,
            .sidebar-container:hover .nav-link span,
            .sidebar-container:hover .user-info span {
                opacity: 1;
                visibility: visible;
            }

            .sidebar-container .nav-link i {
                min-width: 24px;
                text-align: center;
                margin-right: 8px;
            }

            body {
                margin-left: 60px;
                transition: margin-left 0.3s ease-in-out;
            }

            .sidebar-container:hover ~ main {
                margin-left: 250px;
            }

            main {
                transition: margin-left 0.3s ease-in-out;
                padding: 1.5rem;
            }

            @media (max-width: 768px) {
                .sidebar-container {
                    width: 0;
                    left: -60px;
                }
                .sidebar-container:hover {
                    width: 0;
                }
                body {
                    margin-left: 0;
                }
                .sidebar-container:hover ~ main {
                    margin-left: 0;
                }
            }

            .star-rating {
                display: inline-flex;
                flex-direction: row-reverse;
                font-size: 1.5rem;
                cursor: pointer;
            }

            .star-rating input {
                display: none;
            }

            .star-rating label {
                color: #d1d5db;
                transition: color 0.2s ease-in-out, transform 0.2s ease-in-out;
                margin-right: 0.25rem;
            }

            .star-rating input:checked ~ label,
            .star-rating label:hover,
            .star-rating label:hover ~ label {
                color: #facc15;
            }

            .star-rating label:hover {
                transform: scale(1.1);
            }

            .review-stars {
                display: inline-flex;
                font-size: 1rem;
                margin-top: 0.25rem;
            }

            .review-stars .fa-star {
                margin-right: 0.25rem;
            }
        </style>
    </head>
    <body>
        <div>
            <jsp:include page="/layout/sidebar_user.jsp" />

            <jsp:include page="/layout/header.jsp" />

        </div>

        <main class="ml-24 p-6">
            <div class="max-w-6xl mx-auto">
                <c:if test="${not empty error}">
                    <div class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded" role="alert">
                        ${error}
                    </div>
                </c:if>

                <c:if test="${not empty course}">
                    <div class="bg-white rounded-lg shadow-lg overflow-hidden mb-6">
                        <div class="md:flex">
                            <div class="md:w-1/2">
                                <img src="${course.imageDataURI}"
                                     alt="${course.courseName}"
                                     class="w-full h-64 md:h-full object-cover"
                                     onerror="this.src='https://placehold.co/600x400/38bdf8/ffffff?text=Course'">
                            </div>
                            <div class="md:w-1/2 p-6">
                                <div class="mb-4">
                                    <span class="inline-block bg-blue-100 text-blue-800 text-sm font-semibold px-3 py-1 rounded-full">
                                        ${course.category.name}
                                    </span>
                                </div>
                                <h1 class="text-3xl font-bold text-gray-900 mb-4">${course.courseName}</h1>

                                <div class="flex items-center mb-4">
                                    <c:choose>
                                        <c:when test="${not empty course.user.imageDataURI}">
                                            <img src="${course.user.imageDataURI}"
                                                 alt="${course.user.displayName}"
                                                 class="w-12 h-12 rounded-full mr-3 object-cover"
                                                 onerror="this.src='https://i.pravatar.cc/48?u=${course.user.displayName}'">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="https://i.pravatar.cc/48?u=${course.user.displayName}"
                                                 alt="${course.user.displayName}"
                                                 class="w-12 h-12 rounded-full mr-3 object-cover">
                                        </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <a class="link-opacity-100-hover" href="${pageContext.request.contextPath}/viewprofile?id=${course.user.userId}">
                                            <p class="text-gray-900 font-semibold">Instructor: ${course.user.displayName}</p>
                                            <p class="text-gray-600 text-sm">Course Creator</p>
                                        </a>
                                    </div>
                                </div>

                                <div class="mb-6">
                                    <c:choose>  
                                        <c:when test="${course.salePrice == 0 && course.isSale == 1}">
                                            <span class="text-lg font-bold text-green-600">Free</span>
                                        </c:when>
                                        <c:when test="${course.isSale == 1}">                                     
                                            <div class="flex items-center space-x-2">
                                                <fmt:setLocale value="en_US"/>
                                                <span class="text-3xl font-bold text-green-600">
                                                    <fmt:formatNumber value="${course.salePrice}" type="number" groupingUsed="true" maxFractionDigits="0"/> VND
                                                </span>
                                                <span class="text-lg text-gray-500 line-through">
                                                    <fmt:formatNumber value="${course.originalPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/> VND
                                                </span>
                                            </div>
                                        </c:when>
                                        <c:when test="${course.originalPrice == 0 && course.isSale == 0}">
                                            <span class="text-3xl font-bold text-green-600">Free</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-3xl font-bold text-blue-600">
                                                <fmt:setLocale value="en_US"/>
                                                <fmt:formatNumber value="${course.originalPrice}" type="currency" currencySymbol="" groupingUsed="true"/> VND
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="flex space-x-4">
                                    <c:choose>
                                        <c:when test="${sessionScope.user == null}">
                                            <!-- Show login prompt for non-logged-in users -->
                                            <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 w-full">
                                                <div class="flex items-center mb-2">
                                                    <i class="fas fa-info-circle text-yellow-600 mr-2"></i>
                                                    <h4 class="text-lg font-semibold text-yellow-800">Login Required</h4>
                                                </div>
                                                <p class="text-yellow-700 mb-3">Please login to purchase or enroll in this course.</p>
                                                <button onclick="location.href = '<%= request.getContextPath()%>/login'" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors">
                                                    Login to Continue
                                                </button>
                                            </div>
                                        </c:when>
                                        <c:when test="${isEnroll}">
                                            <button onclick="location.href = '<%= request.getContextPath()%>/learner/course?courseID=${course.courseID}'" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors">
                                                To Course
                                            </button>
                                        </c:when>
                                        <c:when test="${isBought}">
                                            <form method="POST" action="<%= request.getContextPath()%>/learner/course">
                                                <button type="submit" name="Enroll" value="${course.courseID}" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors">
                                                    Enroll Now
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:when test="${isInCart}">
                                            <form method="POST" action="<%= request.getContextPath()%>/checkout">
                                                <button type="submit" name="buynowid" value="${course.courseID}" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors">
                                                    Buy Now
                                                </button>
                                            </form>
                                            <button onclick="location.href = '<%= request.getContextPath()%>/cart'" class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-3 px-6 rounded-lg transition-colors">
                                                To Cart
                                            </button>
                                        </c:when>
                                        <c:when test="${(course.salePrice == 0 && course.isSale == 1) || (course.originalPrice == 0 && course.isSale == 0)}">
                                            <form method="POST" action="<%= request.getContextPath()%>/learner/course">
                                                <button type="submit" name="Enroll" value="${course.courseID}" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors">
                                                    Enroll Now
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="POST" action="<%= request.getContextPath()%>/checkout">
                                                <button type="submit" name="buynowid" value="${course.courseID}" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors">
                                                    Buy Now
                                                </button>
                                            </form>
                                            <form method="POST" action="<%= request.getContextPath()%>/cart">
                                                <input type="hidden" name="CourseID" value="${course.courseID}">
                                                <button type="submit" name="CartAction" value="Add" class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-3 px-6 rounded-lg transition-colors">
                                                    Add to Cart
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="bg-white rounded-lg shadow-lg">
                        <div class="border-b border-gray-200">
                            <nav class="-mb-px flex space-x-8 px-6">
                                <button class="tab-button active border-b-2 border-blue-500 py-4 px-1 text-blue-600 font-medium" data-tab="overview">
                                    Overview
                                </button>
                                <button class="tab-button border-b-2 border-transparent py-4 px-1 text-gray-500 hover:text-gray-700 font-medium" data-tab="curriculum">
                                    Curriculum
                                </button>
                                <button class="tab-button border-b-2 border-transparent py-4 px-1 text-gray-500 hover:text-gray-700 font-medium" data-tab="reviews">
                                    Reviews
                                </button>
                            </nav>
                        </div>

                        <div class="p-6">
                            <div id="overview" class="tab-content">
                                <h3 class="text-xl font-bold text-gray-900 mb-4">Course Summary</h3>
                                <div class="prose max-w-none">
                                    <p class="text-gray-700 leading-relaxed mb-6">
                                        ${course.courseSummary}
                                    </p>
                                </div>

                                <c:if test="${not empty course.courseHighlight}">
                                    <h3 class="text-xl font-bold text-gray-900 mb-4">Course Highlights</h3>
                                    <div class="prose max-w-none">
                                        <p class="text-gray-700 leading-relaxed">
                                            ${course.courseHighlight}
                                        </p>
                                    </div>
                                </c:if>

                                <div class="mt-8 grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div class="bg-gray-50 p-4 rounded-lg">
                                        <h4 class="font-semibold text-gray-900 mb-2">Course Information</h4>
                                        <ul class="space-y-2 text-sm text-gray-600">
                                            <li><strong>Category:</strong> ${course.category.name}</li>
                                            <li><strong>Instructor:</strong> ${course.user.displayName}</li>
                                            <li><strong>Last Updated:</strong>
                                                <fmt:formatDate value="${course.courseLastUpdate}" pattern="MMM dd, yyyy"/>
                                            </li>
                                            <li><strong>Published:</strong>
                                                <fmt:formatDate value="${course.publicDate}" pattern="MMM dd, yyyy"/>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div id="curriculum" class="tab-content hidden">
                                <h3 class="text-xl font-bold text-gray-900 mb-4">Course Curriculum</h3>
                                <p class="text-gray-600">Course curriculum will be displayed here when available.</p>
                            </div>

                            <div id="reviews" class="tab-content hidden">
                                <h3 class="text-xl font-bold text-gray-900 mb-4">Student Reviews</h3>

                                <!-- Review Summary -->
                                <div class="bg-gray-50 p-4 rounded-lg mb-6">
                                    <div class="flex items-center mb-4">
                                        <span class="text-2xl font-bold text-gray-900 mr-2">
                                            <c:choose>
                                                <c:when test="${not empty averageRating}"><fmt:formatNumber value="${averageRating}" pattern="#.#"/></c:when>
                                                <c:otherwise>0.0</c:otherwise>
                                            </c:choose>
                                        </span>
                                        <div class="review-stars">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="fas fa-star ${not empty averageRating && averageRating >= i ? 'text-yellow-400' : 'text-gray-300'}"></i>
                                            </c:forEach>
                                        </div>
                                        <span class="text-gray-600 ml-2">(${totalReviews} reviews)</span>
                                    </div>
                                    <div class="space-y-2">
                                        <c:forEach begin="1" end="5" var="i">
                                            <div class="flex items-center">
                                                <span class="w-12 text-sm">${i} Star${i > 1 ? 's' : ''}</span>
                                                <div class="w-full bg-gray-200 rounded-full h-2.5">
                                                    <div class="bg-blue-600 h-2.5 rounded-full" style="width: ${totalReviews > 0 ? (ratingCounts[i] * 100.0 / totalReviews) : 0}%"></div>
                                                </div>
                                                <span class="ml-2 text-sm">${ratingCounts[i]}</span>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="mt-4">
                                        <label for="reviewFilter" class="text-sm font-medium text-gray-700 mr-2">Filter by:</label>
                                        <select id="reviewFilter" class="border-gray-300 rounded-md p-2 text-sm focus:ring-blue-500 focus:border-blue-500">
                                            <option value="all">All Reviews</option>
                                            <c:forEach begin="1" end="5" var="i">
                                                <option value="${i}">${i} Star${i > 1 ? 's' : ''}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <!-- Review Submission Form -->
                                <c:if test="${studyProgress >= 50 && !hasReviewed}">
                                    <div class="bg-white p-6 rounded-lg mb-6 shadow-md">
                                        <h4 class="text-lg font-semibold text-gray-900 mb-4">Submit Your Review</h4>
                                        <form id="reviewForm" method="POST" action="<%= request.getContextPath() %>/courseDetail">
                                            <input type="hidden" name="courseID" value="${course.courseID}">
                                            <input type="hidden" name="userID" value="${user.userId}">
                                            <input type="hidden" name="rate" id="rating-value" value="0">
                                            <div class="mb-4">
                                                <label class="block text-sm font-medium text-gray-700 mb-2">Rating</label>
                                                <div class="star-rating">
                                                    <input type="radio" id="star5" name="star-rating" value="5" onclick="setRating(5)">
                                                    <label for="star5" class="fas fa-star"></label>
                                                    <input type="radio" id="star4" name="star-rating" value="4" onclick="setRating(4)">
                                                    <label for="star4" class="fas fa-star"></label>
                                                    <input type="radio" id="star3" name="star-rating" value="3" onclick="setRating(3)">
                                                    <label for="star3" class="fas fa-star"></label>
                                                    <input type="radio" id="star2" name="star-rating" value="2" onclick="setRating(2)">
                                                    <label for="star2" class="fas fa-star"></label>
                                                    <input type="radio" id="star1" name="star-rating" value="1" onclick="setRating(1)">
                                                    <label for="star1" class="fas fa-star"></label>
                                                </div>
                                            </div>
                                            <div class="mb-4">
                                                <label for="reviewDescription" class="block text-sm font-medium text-gray-700">Feedback</label>
                                                <textarea id="reviewDescription" name="reviewDescription" rows="4" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2" placeholder="Share your thoughts about the course..."></textarea>
                                            </div>
                                            <button type="submit" id="submitReview" class="bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition-colors" disabled>Submit Review</button>
                                        </form>
                                    </div>
                                </c:if>

                                <!-- Display Reviews -->
                                <div id="reviewList" class="space-y-6">
                                    <c:choose>
                                        <c:when test="${not empty reviewList}">
                                            <c:forEach var="review" items="${reviewList}">
                                                <div class="review-item bg-white p-4 rounded-lg shadow-md" data-rating="${review.rate}">
                                                    <div class="flex items-center mb-2">
                                                        <c:choose>
                                                            <c:when test="${not empty review.user.avatarUrl || not empty review.user.imageDataURI}">
                                                                <img src="${not empty review.user.avatarUrl ? review.user.avatarUrl : review.user.imageDataURI}" alt="${review.user.displayName}" class="w-10 h-10 rounded-full mr-3 object-cover" onerror="this.src='https://i.pravatar.cc/48?u=${review.user.displayName}'">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="https://i.pravatar.cc/48?u=${review.user.displayName}" alt="${review.user.displayName}" class="w-10 h-10 rounded-full mr-3 object-cover">
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div>
                                                            <a class="link-opacity-100-hover text-gray-900 font-semibold text-body text-decoration-none" href="${pageContext.request.contextPath}/viewprofile?id=${review.user.userId}">
                                                                ${review.user.displayName}
                                                            </a>
                                                            <div class="review-stars text-sm">
                                                                <c:forEach begin="1" end="5" var="i">
                                                                    <i class="fas fa-star ${review.rate >= i ? 'text-yellow-400' : 'text-gray-300'}"></i>
                                                                </c:forEach>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <p class="text-gray-700">${review.reviewDescription}</p>
                                                </div>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="text-gray-600">No reviews available for this course yet.</p>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${empty course}">
                    <div class="bg-white rounded-lg shadow-lg p-8 text-center">
                        <i class="fas fa-exclamation-triangle fa-3x text-yellow-500 mb-4"></i>
                        <h2 class="text-2xl font-bold text-gray-900 mb-2">Course Not Found</h2>
                        <p class="text-gray-600 mb-4">The course you're looking for doesn't exist or has been removed.</p>
                        <a href="AllCourses" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                            Browse All Courses
                        </a>
                    </div>
                </c:if>
            </div>
        </main>
        <footer>
            <jsp:include page="/layout/footer.jsp" />
        </footer>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Tab functionality
                const tabButtons = document.querySelectorAll('.tab-button');
                const tabContents = document.querySelectorAll('.tab-content');

                tabButtons.forEach(button => {
                    button.addEventListener('click', () => {
                        const targetTab = button.getAttribute('data-tab');
                        tabButtons.forEach(btn => {
                            btn.classList.remove('active', 'border-blue-500', 'text-blue-600');
                            btn.classList.add('border-transparent', 'text-gray-500');
                        });
                        button.classList.add('active', 'border-blue-500', 'text-blue-600');
                        button.classList.remove('border-transparent', 'text-gray-500');
                        tabContents.forEach(content => {
                            content.classList.add('hidden');
                        });
                        document.getElementById(targetTab).classList.remove('hidden');
                    });
                });

                // Review rating validation
                const ratingInputs = document.querySelectorAll('.star-rating input');
                const submitButton = document.getElementById('submitReview');
                const reviewForm = document.getElementById('reviewForm');
                const reviewDescription = document.getElementById('reviewDescription');

                function updateSubmitButton() {
                    const ratingValue = parseInt(document.getElementById('rating-value').value);
                    const descriptionValue = reviewDescription ? reviewDescription.value.trim() : '';
                    const isValid = ratingValue >= 1 && ratingValue <= 5 && descriptionValue !== '';
                    submitButton.disabled = !isValid;
                    console.log('Rating:', ratingValue, 'Description:', descriptionValue, 'Button Enabled:', isValid);
                }

                // Initialize button state
                if (submitButton) {
                    updateSubmitButton();
                }

                // Update on rating change
                ratingInputs.forEach(input => {
                    input.addEventListener('click', () => {
                        setRating(parseInt(input.value));
                    });
                });

                // Update on description change
                if (reviewDescription) {
                    reviewDescription.addEventListener('input', updateSubmitButton);
                }

                function setRating(value) {
                    document.getElementById('rating-value').value = value;
                    updateSubmitButton();
                }

                // Review filter
                const reviewFilter = document.getElementById('reviewFilter');
                const reviewItems = document.querySelectorAll('.review-item');
                const noReviewsMessage = document.querySelector('#reviewList p');

                reviewFilter.addEventListener('change', function () {
                    const selectedRating = this.value;
                    let visibleReviews = 0;

                    reviewItems.forEach(item => {
                        const rating = parseFloat(item.getAttribute('data-rating'));
                        if (selectedRating === 'all' || Math.round(rating) === parseInt(selectedRating)) {
                            item.style.display = 'block';
                            visibleReviews++;
                        } else {
                            item.style.display = 'none';
                        }
                    });

                    if (noReviewsMessage) {
                        noReviewsMessage.style.display = visibleReviews > 0 ? 'none' : 'block';
                    }
                });
            });
        </script>
    </body>
</html>
