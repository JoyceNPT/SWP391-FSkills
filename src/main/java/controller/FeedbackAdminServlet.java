package controller;

import dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Feedback;
import model.Role;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "FeedbackAdminServlet", urlPatterns = {"/admin/feedback"})
public class FeedbackAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request, response)) return;

        String feedbackType = getOrDefault(request.getParameter("type"), "comments");
        String sortBy = getOrDefault(request.getParameter("sort"), "newest");

        FeedbackDAO dao = new FeedbackDAO();
        List<Feedback> feedbackList = "all".equals(feedbackType)
                ? dao.getAllFeedback()
                : dao.getFeedbackByType(feedbackType);

        feedbackList.sort((a, b) -> "oldest".equals(sortBy)
                ? a.getTimestamp().compareTo(b.getTimestamp())
                : b.getTimestamp().compareTo(a.getTimestamp()));

        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("activeTab", feedbackType);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("feedbackCount", feedbackList.size());

        request.getRequestDispatcher("/WEB-INF/views/feedback_admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request, response)) return;

        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter");
            return;
        }

        FeedbackDAO dao = new FeedbackDAO();
        HttpSession session = request.getSession();

        if ("deleteAll".equals(action)) {
            int deleted = dao.deleteAllFeedback();
            setSessionMessage(session, deleted >= 0,
                    "All feedback deleted successfully. " + deleted + " records removed.",
                    "Failed to delete all feedback");
            redirectBack(request, response);
            return;
        }

        int feedbackId = parseIntParam(request.getParameter("id"));
        if (feedbackId == -1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or missing feedback ID");
            return;
        }

        int result = 0;
        switch (action) {
            case "archive":
            case "resolve":
                result = dao.updateFeedbackStatus(feedbackId, action);
                break;
            case "delete":
                result = dao.deleteFeedback(feedbackId);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
                return;
        }


        boolean isSuccess = result > 0;
        if ("delete".equals(action)) {
            setSessionMessage(session, isSuccess,
                    "Feedback deleted successfully",
                    "Failed to delete feedback");
            redirectBack(request, response);
        } else {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": " + isSuccess + ", \"message\": \"" +
                    (isSuccess ? "Feedback " + action + "d successfully" : "Failed to " + action + " feedback") + "\"}");
        }
    }

    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only administrators can access this page.");
            return false;
        }

        return true;
    }

    private void setSessionMessage(HttpSession session, boolean success, String successMsg, String errorMsg) {
        session.setAttribute(success ? "success" : "err", success ? successMsg : errorMsg);
    }

    private void redirectBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = request.getContextPath() + "/admin/feedback";
        String type = request.getParameter("type");
        if (type != null && !type.isEmpty()) {
            redirectUrl += "?type=" + type;
        }
        response.sendRedirect(redirectUrl);
    }

    private int parseIntParam(String param) {
        try {
            return param != null ? Integer.parseInt(param) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String getOrDefault(String value, String def) {
        return value == null || value.isEmpty() ? def : value;
    }

    @Override
    public String getServletInfo() {
        return "Admin Feedback Management Servlet";
    }
}
