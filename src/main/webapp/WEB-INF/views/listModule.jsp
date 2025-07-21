<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>List Module | F-Skill</title>
        <meta charset="UTF-8">
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon_io/favicon.ico">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
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

            body {
                background-color: var(--background-color);
                font-family: 'Segoe UI', sans-serif;
            }

            .main {
                transition: margin-left 0.3s ease, width 0.3s ease;
                max-width: 100%;
                box-sizing: border-box;
                padding: 2rem 3vw;
            }

            .table th, .table td {
                vertical-align: middle;
                text-align: center;
                color: var(--primary-color);
            }

            .table thead {
                background-color: var(--primary-color);
                color: white;
            }

            .badge-pending {
                background-color: #ffc107;
            }

            .badge-approved {
                background-color: var(--success-color);
            }

            h2 {
                color: var(--primary-color);
                margin-bottom: 25px;
            }

            .link-hover {
                color: inherit;
                text-decoration: none;
                transition: color 0.2s ease;
            }

            .link-hover:hover {
                color: var(--secondary-color);
                text-decoration: none;
            }

            .info-box img {
                width: 160px;
                height: 100px;
                object-fit: cover;
                border-radius: var(--border-radius);
            }

            .btn-primary {
                background-color: var(--secondary-color);
                border-color: var(--secondary-color);
                transition: var(--transition);
            }

            .btn-primary:hover {
                background-color: #2980b9;
                transform: translateY(-2px);
            }

            .modal-content {
                border-radius: var(--border-radius);
            }

            .modal-table {
                margin-bottom: 0;
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
        </style>
    </head>
    <body>
        <jsp:include page="/layout/sidebar_user.jsp"/>
        <jsp:include page="/layout/header.jsp"/>

        <main class="main">
            <div class="px-5">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item inline-flex items-center"><a class="text-indigo-600 hover:text-indigo-700 font-medium no-underline" href="${pageContext.request.contextPath}/instructor">Dashboard</a></li>
                        <li class="breadcrumb-item inline-flex items-center"><a class="text-indigo-600 hover:text-indigo-700 font-medium no-underline" href="${pageContext.request.contextPath}/instructor/courses?action=list">All Courses</a></li>
                        <li class="breadcrumb-item active" aria-current="page">
                            ${course.courseName}
                        </li>
                    </ol>
                </nav>

                <c:if test="${not empty err}">
                    <div class="alert alert-danger text-center">${err}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success text-center">${success}</div>
                </c:if>

                <div class="d-flex justify-content-between align-items-center mb-3">
                    <a href="${pageContext.request.contextPath}/instructor/courses?action=list" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                    <div>
                        <button type="button" class="btn btn-primary me-2" data-bs-toggle="modal" data-bs-target="#enrolledLearnersModal">
                            <i class="fas fa-users"></i> View Enrolled Learners
                        </button>
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createModuleModal">
                            <i class="fas fa-plus"></i> Create New Module
                        </button>
                    </div>
                </div>

                <div class="info-box d-flex align-items-center gap-4 mb-4">
                    <c:if test="${not empty course}">
                        <img src="${course.imageDataURI}" class="rounded me-3"
                             style="width: 160px; height: 100px; object-fit: cover;" alt="Avatar">
                        <div>
                            <h5 class="mb-1"><strong>Course Name:</strong> ${course.courseName}</h5>
                            <c:if test="${not empty course.category}">
                                <p class="mb-0"><strong>Category:</strong> ${course.category.name}</p>
                            </c:if>
                            <p class="mb-0">
                                <strong>Status:</strong>
                                <c:choose>
                                    <c:when test="${course.approveStatus == 1}">
                                        <span class="badge badge-approved">Approved</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-pending">Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p class="mb-0">
                                <strong>Public Date:</strong>
                                <c:choose>
                                    <c:when test="${not empty course.publicDate}">
                                        <fmt:formatDate value="${course.publicDate}" pattern="HH:mm dd-MM-yyyy"/>
                                    </c:when>
                                    <c:otherwise>N/A</c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${empty listModule}">
                        <div class="alert alert-warning text-center">No modules available.</div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-bordered table-hover shadow-sm bg-white rounded">
                            <thead>
                                <tr>
                                    <th>Module Name</th>
                                    <th>Module Last Update</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="module" items="${listModule}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/instructor/courses/modules/material?moduleId=${module.moduleID}&courseId=${course.courseID}"
                                               class="link-hover">
                                                ${module.moduleName}
                                            </a>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty module.moduleLastUpdate}">
                                                    <span class="datetime" data-utc="${module.moduleLastUpdate}Z"></span>
                                                </c:when>
                                                <c:otherwise>N/A</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="d-flex gap-1">
                                            <a href="${pageContext.request.contextPath}/instructor/courses/modules/material?moduleId=${module.moduleID}&courseId=${course.courseID}"
                                               class="btn btn-sm btn-info text-white">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <button class="btn btn-sm btn-warning" data-bs-toggle="modal"
                                                    data-bs-target="#updateModal${module.moduleID}">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="btn btn-sm btn-danger" data-bs-toggle="modal"
                                                    data-bs-target="#deleteModal${module.moduleID}">
                                                <i class="fas fa-trash-alt"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Enrolled Learners Modal -->
            <div class="modal fade" id="enrolledLearnersModal" tabindex="-1" aria-labelledby="enrolledLearnersModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="enrolledLearnersModalLabel">Enrolled Learners for ${course.courseName}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <table class="table modal-table">
                                <tr>
                                    <th>Total Students</th>
                                    <td><c:out value="${totalStudents}"/></td>
                                </tr>
                                <tr>
                                    <th>Completed Students</th>
                                    <td><c:out value="${completedStudents}"/></td>
                                </tr>
                            </table>
                            <c:choose>
                                <c:when test="${empty enrolledUsers}">
                                    <p class="text-center">No students enrolled.</p>
                                </c:when>
                                <c:otherwise>
                                    <table class="table table-bordered table-hover" id="learnerTable">
                                        <thead>
                                            <tr>
                                                <th>Student Name</th>
                                                <th>Progress</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="userData" items="${enrolledUsers}">
                                                <tr class="learner-item">
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/viewprofile?id=${userData.user.userId}" class="link-hover">
                                                            <c:out value="${not empty userData.user.displayName ? userData.user.displayName : userData.user.userName}"/>
                                                        </a>
                                                    </td>
                                                    <td><c:out value="${userData.progress}%"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                    <div class="pagination">
                                        <button id="prevLearnerPage" disabled>Previous</button>
                                        <button id="nextLearnerPage">Next</button>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Create Module Modal -->
            <div class="modal fade" id="createModuleModal" tabindex="-1" aria-labelledby="createModuleModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <form id="createModuleForm" action="${pageContext.request.contextPath}/instructor/courses/modules?action=create" method="POST">
                            <div class="modal-header">
                                <h1 class="modal-title" id="createModuleModalLabel">Create New Module</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="courseID" value="${course.courseID}"/>
                                <div class="mb-3">
                                    <label for="moduleName" class="form-label">Module Name</label>
                                    <input type="text" class="form-control" id="moduleName" name="moduleName" required>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-success">Create Module</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <c:forEach var="module" items="${listModule}">
                <!-- Update Module Modal -->
                <div class="modal fade" id="updateModal${module.moduleID}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog">
                        <form id="updateModuleForm${module.moduleID}" action="${pageContext.request.contextPath}/instructor/courses/modules?action=update" method="POST" class="modal-content bg-white">
                            <div class="modal-header">
                                <h1 class="modal-title">Update Module</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="moduleID" value="${module.moduleID}"/>
                                <input type="hidden" name="courseID" value="${course.courseID}"/>
                                <div class="mb-3">
                                    <label class="form-label">Module Name</label>
                                    <input type="text" name="moduleName" id="updateModuleName${module.moduleID}" value="${module.moduleName}" class="form-control" required>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-primary">Save changes</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Delete Module Modal -->
                <div class="modal fade" id="deleteModal${module.moduleID}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <form action="${pageContext.request.contextPath}/instructor/courses/modules?action=delete" method="POST" class="modal-content bg-white">
                            <div class="modal-header">
                                <h5 class="modal-title text-danger">Delete Module</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="moduleID" value="${module.moduleID}"/>
                                <input type="hidden" name="courseID" value="${course.courseID}"/>
                                <p>Are you sure you want to delete <strong>${module.moduleName}</strong>?</p>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-danger">Delete</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </main>

        <jsp:include page="/layout/footer.jsp"/>
        <jsp:include page="/layout/toast.jsp" />
        <script src="${pageContext.request.contextPath}/layout/formatUtcToVietnamese.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                // Format UTC dates
                formatUtcToVietnamese(".datetime");

                // Create Module Form Validation
                const createForm = document.getElementById("createModuleForm");
                createForm.addEventListener("submit", function (e) {
                    const nameInput = document.getElementById("moduleName");
                    const name = nameInput.value.trim();
                    const spaceOnlyRegex = /^(?!.* {2,}).+$/u;

                    if (!spaceOnlyRegex.test(name)) {
                        showJsToast("Module name must not contain consecutive spaces.");
                        nameInput.focus();
                        e.preventDefault();
                        return;
                    }

                    if (!name) {
                        showJsToast("Module Name is required.");
                        nameInput.focus();
                        e.preventDefault();
                        return;
                    }

                    if (name.length > 30) {
                        showJsToast("Module Name must not exceed 30 characters.");
                        nameInput.focus();
                        e.preventDefault();
                        return;
                    }

                    nameInput.value = name;
                });

                // Update Module Form Validation
                document.querySelectorAll("form[id^='updateModuleForm']").forEach(function (form) {
                    form.addEventListener("submit", function (e) {
                        const moduleID = form.id.replace("updateModuleForm", "");
                        const nameInput = document.getElementById("updateModuleName" + moduleID);
                        const name = nameInput.value.trim();
                        const spaceOnlyRegex = /^(?!.* {2,}).+$/u;

                        if (!spaceOnlyRegex.test(name)) {
                            showJsToast("Module name must not contain consecutive spaces.");
                            nameInput.focus();
                            e.preventDefault();
                            return;
                        }

                        if (!name) {
                            showJsToast("Module Name is required.");
                            nameInput.focus();
                            e.preventDefault();
                            return;
                        }

                        if (name.length > 30) {
                            showJsToast("Module Name must not exceed 30 characters.");
                            nameInput.focus();
                            e.preventDefault();
                            return;
                        }

                        nameInput.value = name;
                    });
                });

                // Pagination for learners
                const learnerItems = document.querySelectorAll('#learnerTable .learner-item');
                const prevLearnerBtn = document.getElementById('prevLearnerPage');
                const nextLearnerBtn = document.getElementById('nextLearnerPage');
                const itemsPerPage = 6;
                let currentLearnerPage = 1;

                function showLearnerPage(page) {
                    const start = (page - 1) * itemsPerPage;
                    const end = start + itemsPerPage;

                    learnerItems.forEach((item, index) => {
                        item.style.display = (index >= start && index < end) ? 'table-row' : 'none';
                    });

                    prevLearnerBtn.disabled = page === 1;
                    nextLearnerBtn.disabled = end >= learnerItems.length;
                }

                if (learnerItems.length > 0) {
                    showLearnerPage(currentLearnerPage);
                    prevLearnerBtn.addEventListener('click', () => {
                        if (currentLearnerPage > 1) {
                            currentLearnerPage--;
                            showLearnerPage(currentLearnerPage);
                        }
                    });
                    nextLearnerBtn.addEventListener('click', () => {
                        if (currentLearnerPage * itemsPerPage < learnerItems.length) {
                            currentLearnerPage++;
                            showLearnerPage(currentLearnerPage);
                        }
                    });
                }
            });
        </script>
    </body>
</html>