<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Change Email - F-Skills</title>

  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon_io/favicon.ico">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
  <style>
    .change-email-container {
      background-color: white;
      padding: 30px;
      border-radius: 10px;
      box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
      width: 90%;
      max-width: 500px;
      position: relative;
      margin: 0 auto;
    }

    .change-email-container h1 {
      text-align: center;
      margin-bottom: 20px;
      color: #333;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: 500;
    }

    .form-group input {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 16px;
    }

    .password-requirements {
      margin-top: 5px;
      font-size: 0.8em;
    }

    .requirement {
      color: #999;
      margin: 2px 0;
    }

    .requirement.valid {
      color: #28a745;
    }

    .requirement.invalid {
      color: #dc3545;
    }

    button {
      background-color: #007bff;
      color: white;
      border: none;
      padding: 10px 15px;
      border-radius: 5px;
      cursor: pointer;
      font-size: 16px;
      width: 100%;
    }

    button:hover {
      background-color: #0069d9;
    }

    button:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }

    #sendOtpBtn {
      margin-left: 10px;
      width: auto;
    }
  </style>
</head>

<body>
<c:choose>
  <c:when test="${sessionScope.user.role eq 'ADMIN'}">
    <jsp:include page="/layout/sidebar_admin.jsp"/>
  </c:when>
  <c:otherwise>
    <jsp:include page="/layout/sidebar_user.jsp"/>
  </c:otherwise>
</c:choose>

<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-8">
      <div class="card">
        <div class="card-header">
          <h2 class="text-center">Change Email</h2>
        </div>
        <div class="card-body">
          <c:if test="${not empty err}">
            <div class="alert alert-danger" role="alert">
              ${err}
            </div>
          </c:if>

          <form id="emailForm" action="${pageContext.request.contextPath}${sessionScope.user.role eq 'INSTRUCTOR' ? '/instructor/changeEmail' : sessionScope.user.role eq 'ADMIN' ? '/admin/changeEmail' : '/learner/changeEmail'}?action=changeEmail" method="POST">
            <div class="form-group">
              <label for="newEmail">New Email</label>
              <div style="display: flex; align-items: center;">
                <input type="email" id="newEmail" name="newEmail" required>
                <button type="button" id="sendOtpBtn">Send OTP</button>
              </div>
              <div class="password-requirements">
                <p class="requirement" id="email-format">Valid email format required</p>
              </div>
            </div>

            <div class="form-group">
              <label for="otpCode">OTP Code</label>
              <input type="text" id="otpCode" name="otpCode" required>
              <p id="otp-message" class="requirement"></p>
            </div>

            <div class="form-group">
              <button type="submit" id="saveEmailBtn" disabled>Save Change</button>
            </div>

            <div class="form-group text-center">
              <a href="${pageContext.request.contextPath}${sessionScope.user.role eq 'INSTRUCTOR' ? '/instructor/profile' : sessionScope.user.role eq 'ADMIN' ? '/admin/profile' : '/learner/profile'}?action=edit" class="btn btn-secondary">Back to Profile</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Toast Notification -->
<div style="z-index: 2000;" class="toast-container position-fixed bottom-0 end-0 p-3">
  <!-- Error Toast -->
  <div id="serverToast" class="toast align-items-center text-bg-danger border-0" role="alert"
       aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body">
        <c:if test="${not empty err}">${err}</c:if>
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"
              aria-label="Close"></button>
    </div>
  </div>

  <!-- Custom Error Toast -->
  <div id="customErrorToast" class="toast align-items-center text-bg-danger border-0" role="alert"
       aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="customErrorMessage">
        Error message here
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"
              aria-label="Close"></button>
    </div>
  </div>

  <!-- Custom Success Toast -->
  <div id="customSuccessToast" class="toast align-items-center text-bg-success border-0" role="alert"
       aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="customSuccessMessage">
        Success message here
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"
              aria-label="Close"></button>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Email validation and OTP handling
  const newEmailInput = document.getElementById('newEmail');
  const otpCodeInput = document.getElementById('otpCode');
  const sendOtpBtn = document.getElementById('sendOtpBtn');
  const saveEmailBtn = document.getElementById('saveEmailBtn');
  const otpMessage = document.getElementById('otp-message');
  const emailFormatRequirement = document.getElementById('email-format');

  // Add input event listener to validate email as user types
  newEmailInput.addEventListener('input', validateEmail);

  function validateEmail() {
    const email = newEmailInput.value;

    // Validate email format
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|vn|io|me|net|edu|org|info|biz|co|xyz|gov|mil|asia|us|uk|ca|au|edu\.vn|fpt\.edu\.vn)$/;
    const isValidFormat = emailRegex.test(email);

    if (isValidFormat) {
      emailFormatRequirement.classList.add('valid');
    } else {
      emailFormatRequirement.classList.remove('valid');
    }

    // Enable/disable send OTP button based on validation
    sendOtpBtn.disabled = !isValidFormat;
  }

  sendOtpBtn.addEventListener('click', function () {
    // Disable button to prevent multiple clicks
    sendOtpBtn.disabled = true;
    sendOtpBtn.textContent = "Sending...";

    // Clear previous messages
    otpMessage.textContent = "";
    otpMessage.classList.remove('valid', 'invalid');

    const newEmail = newEmailInput.value;

    // Validate email is not empty
    if (!newEmail) {
      otpMessage.textContent = "Please enter a new email.";
      otpMessage.classList.add('invalid');
      sendOtpBtn.disabled = false;
      sendOtpBtn.textContent = "Send";
      return;
    }

    // Validate email format
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|vn|io|me|net|edu|org|info|biz|co|xyz|gov|mil|asia|us|uk|ca|au|edu\.vn|fpt\.edu\.vn)$/;

    if (!emailRegex.test(newEmail)) {
      otpMessage.textContent = "Invalid email format.";
      otpMessage.classList.add('invalid');
      sendOtpBtn.disabled = false;
      sendOtpBtn.textContent = "Send";
      return;
    }

    console.log("Sending OTP request for email: " + newEmail);

    // Send OTP via AJAX
    fetch('${pageContext.request.contextPath}${sessionScope.user.role eq "INSTRUCTOR" ? "/instructor/changeEmail" : sessionScope.user.role eq "ADMIN" ? "/admin/changeEmail" : "/learner/changeEmail"}?action=sendOtp', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: 'newEmail=' + encodeURIComponent(newEmail)
    })
    .then(response => {
      console.log("Response status: " + response.status);

      // Check if response is ok (status in the range 200-299)
      if (!response.ok) {
        throw new Error("Server returned status " + response.status);
      }

      // Try to parse as JSON
      return response.text().then(text => {
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error("Failed to parse JSON response: ", text);
          throw new Error("Invalid response from server");
        }
      });
    })
    .then(data => {
      console.log("Response data: ", data);

      if (data.success) {
        // Success case
        otpMessage.textContent = data.message || "OTP sent to your email.";
        otpMessage.classList.add('valid');
        otpMessage.classList.remove('invalid');
        otpCodeInput.disabled = false;
        otpCodeInput.focus();

        // Show success toast
        const customSuccessMessage = document.getElementById('customSuccessMessage');
        if (customSuccessMessage) {
          customSuccessMessage.textContent = "OTP sent successfully. Please check your email.";
          const toast = new bootstrap.Toast(document.getElementById('customSuccessToast'), {delay: 3000});
          toast.show();
        }
      } else {
        // Error case
        otpMessage.textContent = data.message || "Failed to send OTP.";
        otpMessage.classList.add('invalid');
        otpMessage.classList.remove('valid');

        // Show error toast
        const customErrorMessage = document.getElementById('customErrorMessage');
        if (customErrorMessage) {
          customErrorMessage.textContent = data.message || "Failed to send OTP.";
          const toast = new bootstrap.Toast(document.getElementById('customErrorToast'), {delay: 3000});
          toast.show();
        }
      }
    })
    .catch(error => {
      console.error("Error sending OTP: ", error);
      otpMessage.textContent = "Error sending OTP: " + error.message;
      otpMessage.classList.add('invalid');

      // Show error toast
      const customErrorMessage = document.getElementById('customErrorMessage');
      if (customErrorMessage) {
        customErrorMessage.textContent = "Error sending OTP: " + error.message;
        const toast = new bootstrap.Toast(document.getElementById('customErrorToast'), {delay: 3000});
        toast.show();
      }
    })
    .finally(() => {
      // Re-enable button
      sendOtpBtn.disabled = false;
      sendOtpBtn.textContent = "Send";
    });
  });

  otpCodeInput.addEventListener('input', function () {
    const otp = otpCodeInput.value;
    if (otp.length >= 6) {
      saveEmailBtn.disabled = false;
    } else {
      saveEmailBtn.disabled = true;
    }
  });

  // Initialize toast notifications
  document.addEventListener('DOMContentLoaded', function () {
    if ('${not empty err}' === 'true') {
      const toastEl = document.getElementById('serverToast');
      if (toastEl) {
        const bsToast = new bootstrap.Toast(toastEl, {delay: 5000});
        bsToast.show();
      }
    }
  });
</script>
</body>
</html>
