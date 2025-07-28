<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Manage Tests - Admin - FSkills</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">

  <style>
    .badge-info { background-color: #17a2b8; }
    .badge-pass { background-color: #28a745; }
    .badge-warning { background-color: #ffc107; }
    h2 { color: #343a40; margin-bottom: 25px; }
    .link-hover { color: inherit; text-decoration: none; transition: color 0.2s ease; }
    .link-hover:hover { color: #0d6efd; text-decoration: none; }
    .main { transition: margin-left 0.3s ease, width 0.3s ease; max-width: 100%; box-sizing: border-box; background-color: #ffffff; }
    .btn-sm { font-size: 0.8rem; padding: 0.375rem 0.75rem; }
    .action-buttons { display: flex; gap: 5px; justify-content: center; }
    body { background-color: #ffffff; }
  </style>
</head>
<body>
<jsp:include page="/layout/sidebar_admin.jsp"/>
<jsp:include page="/layout/header.jsp"/>

<main class="main">
  <div class="px-5 py-6">
    <nav class="text-base text-gray-500 mb-6" aria-label="Breadcrumb">
      <ol class="list-none p-0 inline-flex space-x-2">
        <li class="inline-flex items-center">
          <a href="${pageContext.request.contextPath}/admin" class="text-indigo-600 hover:text-indigo-700 font-medium no-underline">Dashboard</a>
        </li>
        <li class="inline-flex items-center">
          <span class="mx-2 text-gray-400">/</span>
        </li>
        <li class="inline-flex items-center">
          <span class="text-gray-800 font-semibold">Manage Tests</span>
        </li>
      </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-3">
      <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Back
      </a>
    </div>

    <c:if test="${not empty sessionScope.success}">
      <div class="alert alert-success alert-dismissible fade show" role="alert">
          ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="success" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.err}">
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
          ${sessionScope.err}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="err" scope="session"/>
    </c:if>

    <div class="card mb-4">
      <div class="card-body">
        <h5 class="card-title"><i class="fas fa-filter"></i> Filter Tests</h5>
        <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/admin/tests">
          <input type="hidden" name="action" value="list">
          <div class="row">
            <div class="col-md-4">
              <div class="mb-3">
                <label for="filterCourse" class="form-label">Course</label>
                <select class="form-select" id="filterCourse" name="filterCourseId" onchange="loadFilterModules()">
                  <option value="">All Courses</option>
                  <c:forEach var="course" items="${courses}">
                    <option value="${course.courseID}" ${param.filterCourseId == course.courseID ? 'selected' : ''}>${course.courseName}</option>
                  </c:forEach>
                </select>
              </div>
            </div>
            <div class="col-md-4">
              <div class="mb-3">
                <label for="filterModule" class="form-label">Module</label>
                <select class="form-select" id="filterModule" name="filterModuleId">
                  <option value="">All Modules</option>
                  <c:if test="${not empty filterModules}">
                    <c:forEach var="module" items="${filterModules}">
                      <option value="${module.moduleID}" ${param.filterModuleId == module.moduleID ? 'selected' : ''}>${module.moduleName}</option>
                    </c:forEach>
                  </c:if>
                </select>
              </div>
            </div>
            <div class="col-md-4">
              <div class="mb-3">
                <label class="form-label">&nbsp;</label>
                <div>
                  <button type="submit" class="btn btn-primary">
                    <i class="fas fa-search"></i> Filter
                  </button>
                  <a href="${pageContext.request.contextPath}/admin/tests?action=list" class="btn btn-outline-secondary">
                    <i class="fas fa-times"></i> Clear
                  </a>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>

    <c:choose>
      <c:when test="${empty listTest}">
        <div class="alert alert-warning text-center">No tests available.</div>
      </c:when>
      <c:otherwise>
        <table class="table table-bordered table-hover shadow-sm bg-white rounded">
          <thead>
          <tr>
            <th>Test #</th><th>Test Name</th><th>Module</th><th>Course</th><th>Test Order</th><th>Questions</th><th>Pass %</th><th>Randomize</th><th>Show Answer</th><th>Last Update</th><th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="test" items="${listTest}">
            <tr>
              <td><strong>#${test.testID}</strong></td>
              <td><strong>${test.testName}</strong></td>
              <td><c:if test="${not empty test.module}">${test.module.moduleName}</c:if></td>
              <td><c:if test="${not empty test.module && not empty test.module.course}">${test.module.course.courseName}</c:if></td>
              <td><span class="badge badge-info">${test.testOrder}</span></td>
              <td><span class="badge bg-secondary">${test.questionCount} questions</span></td>
              <td><span class="badge badge-pass">${test.passPercentage}%</span></td>
              <td><c:choose><c:when test="${test.randomize}"><i class="fas fa-check text-success"></i></c:when><c:otherwise><i class="fas fa-times text-danger"></i></c:otherwise></c:choose></td>
              <td><c:choose><c:when test="${test.showAnswer}"><i class="fas fa-check text-success"></i></c:when><c:otherwise><i class="fas fa-times text-danger"></i></c:otherwise></c:choose></td>
              <td><c:choose><c:when test="${not empty test.testLastUpdate}"><span class="datetime" data-utc="${test.testLastUpdate}Z"></span></c:when><c:otherwise>N/A</c:otherwise></c:choose></td>
              <td>
                <div class="action-buttons">
                  <a href="${pageContext.request.contextPath}/admin/tests?action=view&testId=${test.testID}" class="btn btn-sm btn-info"><i class="fas fa-eye"></i></a>
                  <button type="button" class="btn btn-sm btn-danger" onclick="confirmDelete(${test.testID})"><i class="fas fa-trash"></i></button>
                </div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </div>
</main>

<!-- Delete Modal, scripts and formatters -->
<jsp:include page="/layout/footer.jsp"/>
<jsp:include page="/layout/toast.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/layout/formatUtcToVietnamese.js"></script>
<script>
  function confirmDelete(testId) {
    document.getElementById('deleteTestId').value = testId;
    document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/admin/tests';
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
  }
  function loadFilterModules() {
    const courseSelect = document.getElementById('filterCourse');
    const moduleSelect = document.getElementById('filterModule');
    moduleSelect.innerHTML = '<option value="">Loading...</option>';
    const courseId = courseSelect.value;
    if (!courseId) return;
    fetch('${pageContext.request.contextPath}/admin/tests?action=getModules&courseId=' + courseId)
            .then(response => response.json())
            .then(modules => {
              moduleSelect.innerHTML = '<option value="">All Modules</option>';
              modules.forEach(module => {
                const option = document.createElement('option');
                option.value = module.moduleID;
                option.textContent = module.moduleName;
                moduleSelect.appendChild(option);
              });
            })
            .catch(error => {
              console.error('Error loading modules:', error);
              moduleSelect.innerHTML = '<option value="">Error loading</option>';
            });
  }
  document.addEventListener('DOMContentLoaded', function() {
    const filterCourse = document.getElementById('filterCourse');
    const filterModule = document.getElementById('filterModule');
    if (filterCourse && filterCourse.value) {
      const selectedModuleId = filterModule.value || '${param.filterModuleId}';
      fetch('${pageContext.request.contextPath}/admin/tests?action=getModules&courseId=' + filterCourse.value)
              .then(response => response.json())
              .then(modules => {
                filterModule.innerHTML = '<option value="">All Modules</option>';
                modules.forEach(module => {
                  const option = document.createElement('option');
                  option.value = module.moduleID;
                  option.textContent = module.moduleName;
                  if (module.moduleID == selectedModuleId) {
                    option.selected = true;
                  }
                  filterModule.appendChild(option);
                });
              });
    }
  });
</script>
</body>
</html>
