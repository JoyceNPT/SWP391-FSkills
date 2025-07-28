<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>View Test | Admin | F-Skill</title>
  <meta charset="UTF-8">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon_io/favicon.ico">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">

  <style>
    body {
      background-color: #f8f9fa;
      font-family: 'Segoe UI', sans-serif;
    }

    h2 {
      color: #343a40;
      margin-bottom: 25px;
    }

    .test-info {
      background: white;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 20px;
      border: 1px solid #dee2e6;
    }

    .question-card {
      border: 1px solid #dee2e6;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      background-color: white;
    }

    .question-header {
      background-color: #4f46e5;
      color: white;
      padding: 10px 15px;
      border-radius: 6px;
      margin-bottom: 15px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .option {
      padding: 8px 0;
      margin: 5px 0;
    }

    .correct-option {
      background-color: #d4edda;
      border: 1px solid #c3e6cb;
      border-radius: 4px;
      padding: 8px 12px;
      margin: 5px 0;
      color: #155724;
    }

    .incorrect-option {
      background-color: #f8f9fa;
      border: 1px solid #dee2e6;
      border-radius: 4px;
      padding: 8px 12px;
      margin: 5px 0;
      color: #6c757d;
    }

    .stats-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 20px;
      border-radius: 8px;
      text-align: center;
    }

    .badge-custom {
      font-size: 0.9rem;
      padding: 8px 12px;
    }

    .correct-answer-icon {
      color: #28a745;
      margin-right: 8px;
    }

    .main {
      transition: margin-left 0.3s ease, width 0.3s ease;
      max-width: 100%;
      box-sizing: border-box;
      background-color: #ffffff;
    }
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
          <a href="${pageContext.request.contextPath}/admin"
             class="text-indigo-600 hover:text-indigo-700 font-medium no-underline">Dashboard</a>
        </li>
        <li class="inline-flex items-center">
          <span class="mx-2 text-gray-400">/</span>
        </li>
        <li class="inline-flex items-center">
          <a href="${pageContext.request.contextPath}/admin/tests"
             class="text-indigo-600 hover:text-indigo-700 font-medium no-underline">Manage Tests</a>
        </li>
        <li class="inline-flex items-center">
          <span class="mx-2 text-gray-400">/</span>
        </li>
        <li class="inline-flex items-center">
          <span class="text-gray-800 font-semibold">View Test</span>
        </li>
      </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-3">
      <a href="${pageContext.request.contextPath}/admin/tests" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Back to Tests
      </a>

      <div class="d-flex gap-2">
        <button type="button" class="btn btn-danger" onclick="confirmDelete(${test.testID})">
          <i class="fas fa-trash"></i> Delete Test
        </button>
      </div>
    </div>

    <h2 class="mb-4 fw-bold fs-3">
      <i class="fas fa-eye"></i> View Test: ${test.testName}
    </h2>

    <!-- Test Information -->
    <div class="test-info">
      <div class="row">
        <div class="col-md-8">
          <h4><i class="fas fa-info-circle"></i> Test Information</h4>
          <div class="row mt-3">
            <div class="col-sm-4"><strong>Test ID:</strong></div>
            <div class="col-sm-8">#${test.testID}</div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Test Name:</strong></div>
            <div class="col-sm-8"><strong>${test.testName}</strong></div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Module:</strong></div>
            <div class="col-sm-8">${test.module.moduleName}</div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Course:</strong></div>
            <div class="col-sm-8">${test.module.course.courseName}</div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Test Order:</strong></div>
            <div class="col-sm-8"><span class="badge bg-primary">${test.testOrder}</span></div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Pass Percentage:</strong></div>
            <div class="col-sm-8"><span class="badge bg-success">${test.passPercentage}%</span></div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Randomize Questions:</strong></div>
            <div class="col-sm-8">
              <c:choose>
                <c:when test="${test.randomize}">
                  <span class="badge bg-info"><i class="fas fa-check"></i> Yes</span>
                </c:when>
                <c:otherwise>
                  <span class="badge bg-secondary"><i class="fas fa-times"></i> No</span>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Show Answers:</strong></div>
            <div class="col-sm-8">
              <c:choose>
                <c:when test="${test.showAnswer}">
                  <span class="badge bg-info"><i class="fas fa-check"></i> Yes</span>
                </c:when>
                <c:otherwise>
                  <span class="badge bg-secondary"><i class="fas fa-times"></i> No</span>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-sm-4"><strong>Last Updated:</strong></div>
            <div class="col-sm-8">
              <c:choose>
                <c:when test="${not empty test.testLastUpdate}">
                  <span class="datetime" data-utc="${test.testLastUpdate}Z"></span>
                </c:when>
                <c:otherwise>N/A</c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="stats-card">
            <h5><i class="fas fa-chart-bar"></i> Test Statistics</h5>
            <div class="row text-center mt-3">
              <div class="col-6">
                <h3 class="mb-1">${questionCount}</h3>
                <small>Questions</small>
              </div>
              <div class="col-6">
                <h3 class="mb-1">${totalPoints}</h3>
                <small>Total Points</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Questions -->
    <div class="mb-4">
      <h4 class="mb-3"><i class="fas fa-question-circle"></i> Questions (${questionCount})</h4>

      <c:choose>
        <c:when test="${empty questions}">
          <div class="alert alert-warning text-center">
            <i class="fas fa-exclamation-triangle"></i> No questions available for this test.
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach var="question" items="${questions}" varStatus="status">
            <div class="question-card">
              <div class="question-header">
                <span><strong>Question ${status.index + 1}</strong></span>
                <div>
                  <span class="badge bg-light text-dark">${question.questionType}</span>
                  <span class="badge bg-warning text-dark">${question.point} point(s)</span>
                </div>
              </div>

              <div class="mb-3">
                <h6 class="fw-bold">${question.question}</h6>
              </div>

              <div class="options">
                <div class="row">
                  <div class="col-md-6">
                    <div class="${question.rightOption == 'A' ? 'correct-option' : 'incorrect-option'}">
                      <c:if test="${question.rightOption == 'A'}">
                        <i class="fas fa-check-circle correct-answer-icon"></i>
                      </c:if>
                      <strong>A.</strong> ${question.option1}
                    </div>

                    <c:if test="${not empty question.option3}">
                      <div class="${question.rightOption == 'C' ? 'correct-option' : 'incorrect-option'}">
                        <c:if test="${question.rightOption == 'C'}">
                          <i class="fas fa-check-circle correct-answer-icon"></i>
                        </c:if>
                        <strong>C.</strong> ${question.option3}
                      </div>
                    </c:if>
                  </div>

                  <div class="col-md-6">
                    <div class="${question.rightOption == 'B' ? 'correct-option' : 'incorrect-option'}">
                      <c:if test="${question.rightOption == 'B'}">
                        <i class="fas fa-check-circle correct-answer-icon"></i>
                      </c:if>
                      <strong>B.</strong> ${question.option2}
                    </div>

                    <c:if test="${not empty question.option4}">
                      <div class="${question.rightOption == 'D' ? 'correct-option' : 'incorrect-option'}">
                        <c:if test="${question.rightOption == 'D'}">
                          <i class="fas fa-check-circle correct-answer-icon"></i>
                        </c:if>
                        <strong>D.</strong> ${question.option4}
                      </div>
                    </c:if>
                  </div>
                </div>
              </div>

              <div class="mt-3 text-end">
                <small class="text-muted">
                  <i class="fas fa-bullseye"></i> Correct Answer:
                  <span class="badge bg-success">${question.rightOption}</span>
                </small>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</main>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        Are you sure you want to delete this test? This action cannot be undone and will also delete all questions associated with this test.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/admin/tests" style="display: inline;">
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="testId" id="deleteTestId">
          <button type="submit" class="btn btn-danger">Delete</button>
        </form>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/layout/footer.jsp"/>
<jsp:include page="/layout/toast.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/layout/formatUtcToVietnamese.js"></script>

<script>
  function confirmDelete(testId) {
    document.getElementById('deleteTestId').value = testId;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
  }

  document.addEventListener('DOMContentLoaded', function() {
    if (typeof formatUtcToVietnamese === 'function') {
      const datetimeElements = document.querySelectorAll('.datetime');
      if (datetimeElements.length > 0) {
        formatUtcToVietnamese('.datetime');
      }
    }
  });

  // Sidebar hover
  document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main');
    const header = document.querySelector('header');

    function handleSidebarMouseEnter() {
      const windowWidth = window.innerWidth;
      mainContent.style.transition = 'margin-left 0.2s ease, width 0.2s ease';

      if (windowWidth <= 768) {
        mainContent.style.marginLeft = '250px';
        if (header) {
          header.style.left = '250px';
          header.style.width = 'calc(100% - 266px)';
        }
      } else {
        mainContent.style.marginLeft = '250px';
        if (header) {
          header.style.left = '250px';
          header.style.width = 'calc(100% - 266px)';
        }
      }
    }

    function handleSidebarMouseLeave() {
      const windowWidth = window.innerWidth;
      mainContent.style.transition = 'margin-left 0.2s ease, width 0.2s ease';

      if (windowWidth <= 768) {
        mainContent.style.marginLeft = '0';
        if (header) {
          header.style.left = '0';
          header.style.width = 'calc(100% - 16px)';
        }
      } else {
        mainContent.style.marginLeft = '80px';
        if (header) {
          header.style.left = '80px';
          header.style.width = 'calc(100% - 96px)';
        }
      }
    }

    sidebar.addEventListener('mouseenter', handleSidebarMouseEnter);
    sidebar.addEventListener('mouseleave', handleSidebarMouseLeave);
    handleSidebarMouseLeave();

    window.addEventListener('resize', function() {
      if (sidebar.matches(':hover')) {
        handleSidebarMouseEnter();
      } else {
        handleSidebarMouseLeave();
      }
    });
  });
</script>

</body>
</html>
