package controller;

import dao.ProfileDAO;
import dao.UserDAO;
import model.Profile;
import model.User;
import util.DBContext;
import util.SendEmail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

@WebServlet(name = "ChangeEmailServlet", urlPatterns = {"/changeEmail", "/learner/changeEmail", "/instructor/changeEmail", "/admin/changeEmail"})
public class ChangeEmailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String OTP_ATTRIBUTE = "otpCode";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (requestURI.startsWith(contextPath + "/learner/changeEmail") && !"LEARNER".equalsIgnoreCase(user.getRole().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only learners can access this page.");
            return;
        } else if (requestURI.startsWith(contextPath + "/instructor/changeEmail") && !"INSTRUCTOR".equalsIgnoreCase(user.getRole().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only instructors can access this page.");
            return;
        } else if (requestURI.startsWith(contextPath + "/admin/changeEmail") && !"ADMIN".equalsIgnoreCase(user.getRole().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only administrators can access this page.");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (requestURI.startsWith(contextPath + "/learner/changeEmail") && !"LEARNER".equalsIgnoreCase(user.getRole().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only learners can access this page.");
            return;
        } else if (requestURI.startsWith(contextPath + "/instructor/changeEmail") && !"INSTRUCTOR".equalsIgnoreCase(user.getRole().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only instructors can access this page.");
            return;
        } else if (requestURI.startsWith(contextPath + "/admin/changeEmail") && !"ADMIN".equalsIgnoreCase(user.getRole().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only administrators can access this page.");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "changeEmail";
        }

        switch (action) {
            case "sendOtp":
                handleSendOtp(request, response, user);
                break;
            case "changeEmail":
                handleChangeEmail(request, response, user);
                break;
            default:
                request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
                break;
        }
    }

    private void handleSendOtp(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String newEmail = request.getParameter("newEmail");

        // Log the request for debugging
        System.out.println("Sending OTP to email: " + newEmail);

        // Validate email parameter
        if (newEmail == null || newEmail.trim().isEmpty()) {
            System.out.println("Error: Email parameter is null or empty");
            response.getWriter().write("{\"success\": false, \"message\": \"Email address is required\"}");
            return;
        }

        // Check if new email is the same as current email
        if (newEmail.equals(user.getEmail())) {
            System.out.println("Error: New email is the same as current email");
            response.getWriter().write("{\"success\": false, \"message\": \"The new email is the same as your current email\"}");
            return;
        }

        // Check if email already exists in the database
        UserDAO userDAO = new UserDAO();
        User existingUser = userDAO.findByEmail(newEmail);
        if (existingUser != null) {
            System.out.println("Error: Email already exists in the database: " + newEmail);
            response.getWriter().write("{\"success\": false, \"message\": \"This email is already in use by another account. Please try a different email address.\"}");
            return;
        }

        // Validate email format
        Profile profile = new Profile();
        profile.setEmail(newEmail);
        if (!profile.validateEmail()) {
            System.out.println("Error: Invalid email format: " + newEmail);
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid email format. Please enter a valid email address.\"}");
            return;
        }

        // Generate OTP
        String otp = generateOtp();
        HttpSession session = request.getSession();

        // Store OTP and email in session with timestamp
        session.setAttribute(OTP_ATTRIBUTE, otp);
        session.setAttribute("newEmail", newEmail);
        session.setAttribute("otpTimestamp", System.currentTimeMillis());

        // Log the OTP for debugging (remove in production)
        System.out.println("Generated OTP: " + otp + " for email: " + newEmail);

        // Prepare email message
        SendEmail sendEmail = new SendEmail();
        String message = "Your OTP code for email change is: <strong>" + otp + "</strong>";

        try {
            // Send email
            boolean sent = sendEmail.sendEmailNormal(newEmail, message, "Email Change Verification");

            if (sent) {
                // Success response
                System.out.println("OTP sent successfully to: " + newEmail);
                response.getWriter().write("{\"success\": true, \"message\": \"OTP sent successfully. Please check your email.\"}");
            } else {
                // Failed to send email
                System.out.println("Failed to send OTP to: " + newEmail);
                response.getWriter().write("{\"success\": false, \"message\": \"Failed to send OTP. Please try again later.\"}");
            }
        } catch (Exception e) {
            // Exception handling
            System.out.println("Exception when sending OTP: " + e.getMessage());
            e.printStackTrace();

            // Send a user-friendly error message
            String errorMessage = "An error occurred while sending the OTP. Please try again later.";
            System.out.println("Sending error response: " + errorMessage);
            response.getWriter().write("{\"success\": false, \"message\": \"" + errorMessage + "\"}");
        }
    }

    private void handleChangeEmail(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String newEmail = request.getParameter("newEmail");
        String otpCode = request.getParameter("otpCode");

        // Log the request for debugging
        System.out.println("Handling change email request for user: " + user.getUserId());
        System.out.println("New email: " + newEmail);
        System.out.println("OTP code: " + otpCode);

        // Validate parameters
        if (newEmail == null || newEmail.trim().isEmpty() || otpCode == null || otpCode.trim().isEmpty()) {
            System.out.println("Error: Email or OTP is null or empty");
            request.setAttribute("err", "Email and OTP code are required");
            request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
            return;
        }

        // Get stored values from session
        String storedOtp = (String) session.getAttribute(OTP_ATTRIBUTE);
        String storedEmail = (String) session.getAttribute("newEmail");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");

        System.out.println("Stored OTP: " + storedOtp);
        System.out.println("Stored email: " + storedEmail);
        System.out.println("OTP timestamp: " + otpTimestamp);

        // Check if OTP exists
        if (storedOtp == null || storedEmail == null) {
            System.out.println("Error: No OTP found in session");
            request.setAttribute("err", "No OTP request found. Please request a new OTP.");
            request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
            return;
        }

        // Check if OTP has expired (10 minutes)
        if (otpTimestamp != null) {
            long currentTime = System.currentTimeMillis();
            long otpAge = currentTime - otpTimestamp;
            long otpMaxAge = 10 * 60 * 1000; // 10 minutes in milliseconds

            if (otpAge > otpMaxAge) {
                System.out.println("Error: OTP has expired. Age: " + (otpAge / 1000) + " seconds");
                request.setAttribute("err", "OTP has expired. Please request a new OTP.");
                session.removeAttribute(OTP_ATTRIBUTE);
                session.removeAttribute("newEmail");
                session.removeAttribute("otpTimestamp");
                request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
                return;
            }
        }

        // Validate OTP and email
        if (!newEmail.equals(storedEmail)) {
            System.out.println("Error: Email mismatch. Input: " + newEmail + ", Stored: " + storedEmail);
            request.setAttribute("err", "Email does not match the one used to request OTP");
            request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
            return;
        }

        if (!otpCode.equals(storedOtp)) {
            System.out.println("Error: Invalid OTP. Input: " + otpCode + ", Stored: " + storedOtp);
            request.setAttribute("err", "Invalid OTP code");
            request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
            return;
        }

        // Double-check if email already exists in the database (in case it was registered between OTP send and verification)
        UserDAO userDAO = new UserDAO();
        User existingUser = userDAO.findByEmail(newEmail);
        if (existingUser != null && existingUser.getUserId() != user.getUserId()) {
            System.out.println("Error: Email already exists in the database: " + newEmail);
            request.setAttribute("err", "This email is already in use by another account. Please try a different email address.");
            request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
            return;
        }

        // Update email in database
        DBContext dbContext = null;
        try {
            dbContext = new DBContext();
            ProfileDAO profileDAO = new ProfileDAO(dbContext);

            System.out.println("Updating email for user " + user.getUserId() + " to: " + newEmail);
            boolean success = profileDAO.updateEmail(user.getUserId(), newEmail);

            if (success) {
                System.out.println("Email updated successfully");

                // Update user in session
                User updatedUser = userDAO.getByUserID(user.getUserId());
                if (updatedUser != null) {
                    session.setAttribute("user", updatedUser);
                    System.out.println("User updated in session");
                }

                // Clean up session attributes
                session.removeAttribute(OTP_ATTRIBUTE);
                session.removeAttribute("newEmail");
                session.removeAttribute("otpTimestamp");

                // Redirect with success message
                if ("INSTRUCTOR".equalsIgnoreCase(user.getRole().toString())) {
                    response.sendRedirect(request.getContextPath() + "/instructor/profile?action=edit&emailSuccess=true");
                } else if ("ADMIN".equalsIgnoreCase(user.getRole().toString())) {
                    response.sendRedirect(request.getContextPath() + "/admin/profile?action=edit&emailSuccess=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/learner/profile?action=edit&emailSuccess=true");
                }
            } else {
                System.out.println("Failed to update email in database");
                request.setAttribute("err", "Failed to update email. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Exception when updating email: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("err", "Error updating email: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/changeEmail.jsp").forward(request, response);
        } finally {
            if (dbContext != null && dbContext.conn != null) {
                try {
                    dbContext.conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
