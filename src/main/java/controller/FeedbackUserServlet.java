package controller;

import dao.FeedbackDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Feedback;
import model.Role;
import model.User;

/**
 * Servlet to handle user feedback
 */
@WebServlet(name = "FeedbackUserServlet", urlPatterns = {"/learner/feedback", "/instructor/feedback", "/feedback"})
public class FeedbackUserServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the feedback form.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is logged in and has appropriate role
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Redirect to login page if user is not logged in
        if (user == null) {
            System.out.println("[DEBUG_LOG] Access denied: User not logged in");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Check if user has learner or instructor role
        Role role = user.getRole();
        if (role != Role.LEARNER && role != Role.INSTRUCTOR) {
            System.out.println("[DEBUG_LOG] Access denied: User role is " + role);
            response.sendRedirect(request.getContextPath() + "/homePage_Guest.jsp");
            return;
        }

        String[] nameParts = user.getDisplayName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        String email = user.getEmail();

        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("email", email);


        // User is logged in and has appropriate role, forward to the feedback JSP page
        request.getRequestDispatcher("/WEB-INF/views/feedback_user.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes the submitted feedback.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is logged in and has appropriate role
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Redirect to login page if user is not logged in
        if (user == null) {
            System.out.println("[DEBUG_LOG] Feedback submission denied: User not logged in");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Check if user has learner or instructor role
        Role role = user.getRole();
        if (role != Role.LEARNER && role != Role.INSTRUCTOR) {
            System.out.println("[DEBUG_LOG] Feedback submission denied: User role is " + role);
            response.sendRedirect(request.getContextPath() + "/homePage_Guest.jsp");
            return;
        }
        
        // Get form data
        String feedbackType = request.getParameter("feedbackType");
        String feedbackTitle = request.getParameter("feedbackTitle");
        String feedbackContent = request.getParameter("feedbackContent");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        // Validate required fields
        if (feedbackContent == null || feedbackContent.trim().isEmpty()) {
            request.setAttribute("err", "Please enter your feedback content.");
            request.getRequestDispatcher("/WEB-INF/views/feedback_user.jsp").forward(request, response);
            return;
        }
        
        // Email validation removed as the field is now hidden and pre-filled from session

        // User is already validated and available from session
        int userId = user.getUserId(); // Get user ID from the authenticated user
            
        // Log user information for debugging
        System.out.println("[DEBUG_LOG] User from session: " + user.getUserId() + ", " + user.getDisplayName() + ", " + user.getEmail());
        System.out.println("[DEBUG_LOG] Form data before override: firstName=" + firstName + ", lastName=" + lastName + ", email=" + email);

        // Always use the user's display name from session
        String[] nameParts = user.getDisplayName().split(" ", 2);
        if (nameParts.length > 0) {
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            } else {
                lastName = "";
            }
        }

        // Always use the user's email from session
        email = user.getEmail();
        
        // Log updated information for debugging
        System.out.println("[DEBUG_LOG] After override: firstName=" + firstName + ", lastName=" + lastName + ", email=" + email);

        // Create feedback object
        Feedback feedback = new Feedback(
                feedbackType,
                feedbackTitle,
                feedbackContent,
                firstName,
                lastName,
                email,
                userId
        );

        // Log feedback details for debugging
        System.out.println("Attempting to save feedback: " + feedback.toString());

        // Save feedback to database
        FeedbackDAO dao = new FeedbackDAO();
        try {
            int result = dao.insertFeedback(feedback);

            if (result > 0) {
                // Success
                System.out.println("Feedback submitted successfully with result: " + result);
                request.setAttribute("success", "Thank you for your feedback! We will review and respond as soon as possible.");
            } else {
                // Error
                System.err.println("Failed to submit feedback. Result: " + result);
                request.setAttribute("err", "Failed to submit feedback. Please try again later.");
            }
        } catch (Exception e) {
            // Log any unexpected exceptions
            System.err.println("Exception during feedback submission: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("err", "An error occurred while processing your feedback. Please try again later.");
        }

        // Forward back to the feedback page
        request.getRequestDispatcher("/WEB-INF/views/feedback_user.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Feedback Servlet";
    }
}
