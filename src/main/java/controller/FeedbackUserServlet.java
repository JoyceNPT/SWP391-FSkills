package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Feedback;
import model.Role;
import model.User;

import java.io.IOException;

@WebServlet(name = "FeedbackUserServlet", urlPatterns = {"/learner/feedback", "/instructor/feedback", "/feedback"})
public class FeedbackUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getValidUser(request, response);
        if (user == null) return;

        String[] nameParts = extractName(user.getDisplayName());
        request.setAttribute("firstName", nameParts[0]);
        request.setAttribute("lastName", nameParts[1]);
        request.setAttribute("email", user.getEmail());

        request.getRequestDispatcher("/WEB-INF/views/feedback_user.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getValidUser(request, response);
        if (user == null) return;

        String feedbackType = request.getParameter("feedbackType");
        String feedbackTitle = request.getParameter("feedbackTitle");
        String feedbackContent = request.getParameter("feedbackContent");

        if (feedbackContent == null || feedbackContent.trim().isEmpty()) {
            request.setAttribute("err", "Please enter your feedback content.");
            forwardBack(request, response);
            return;
        }

        String[] nameParts = extractName(user.getDisplayName());
        Feedback feedback = new Feedback(
                feedbackType,
                feedbackTitle,
                feedbackContent,
                nameParts[0],
                nameParts[1],
                user.getEmail(),
                user.getUserId()
        );

        FeedbackDAO dao = new FeedbackDAO();
        try {
            int result = dao.insertFeedback(feedback);
            if (result > 0) {
                request.setAttribute("success", "Thank you for your feedback! We will review it as soon as possible.");
            } else {
                request.setAttribute("err", "Failed to submit feedback. Please try again later.");
            }
        } catch (Exception e) {
            System.err.println("Feedback submission error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("err", "An error occurred while processing your feedback.");
        }

        forwardBack(request, response);
    }

    private User getValidUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        Role role = user.getRole();
        if (role != Role.LEARNER && role != Role.INSTRUCTOR) {
            response.sendRedirect(request.getContextPath() + "/homePage_Guest.jsp");
            return null;
        }

        return user;
    }

    private String[] extractName(String displayName) {
        String[] nameParts = displayName != null ? displayName.split(" ", 2) : new String[0];
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        return new String[]{firstName, lastName};
    }

    private void forwardBack(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/feedback_user.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Feedback Servlet";
    }
}
