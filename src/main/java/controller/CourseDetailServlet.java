package controller;

import dao.CartDAO;
import dao.CourseDAO;
import dao.EnrollDAO;
import dao.ReviewDAO;
import dao.StudyDAO;
import model.Course;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Review;
import model.User;

@WebServlet("/courseDetail")
public class CourseDetailServlet extends HttpServlet {
    private ReviewDAO reviewDAO;
    private StudyDAO studyDAO;
    private CartDAO cartDAO;
    private EnrollDAO enrollDAO;

    @Override
    public void init() throws ServletException {
        reviewDAO = new ReviewDAO();
        studyDAO = new StudyDAO();
        cartDAO = new CartDAO();
        enrollDAO = new EnrollDAO();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");

        // Get course ID parameter
        String courseIdParam = request.getParameter("id");

        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            response.sendRedirect("AllCourses");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);

            // Get course details from database
            CourseDAO courseDAO = new CourseDAO();
            Course course = courseDAO.getCourseByCourseID(courseId);

            if (course == null) {
                response.sendRedirect("AllCourses");
                return;
            }
            
            // Set course attribute for JSP
            request.setAttribute("course", course);

            // Set attribute for review section
            request.setAttribute("isInCart", cartDAO.isInCart(user.getUserId(), course.getCourseID()));
            request.setAttribute("isBought", enrollDAO.checkBought(user.getUserId(), course.getCourseID()));
            request.setAttribute("isEnroll", enrollDAO.checkEnrollment(user.getUserId(), course.getCourseID()));
            request.setAttribute("reviewList", reviewDAO.getReviewList(course.getCourseID()));
            request.setAttribute("studyProgress", studyDAO.returnStudyProgress(user.getUserId(), course.getCourseID()));
            request.setAttribute("hasReviewed", reviewDAO.isReviewed(user.getUserId(), course.getCourseID()));

            // Forward to course details JSP
            request.getRequestDispatcher("/WEB-INF/views/courseDetailsLearner.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("AllCourses");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseID = Integer.parseInt(request.getParameter("courseID"));
        int userID = Integer.parseInt(request.getParameter("userID"));
        float rate = Float.parseFloat(request.getParameter("rate"));
        String reviewDescription = request.getParameter("reviewDescription");
        ReviewDAO reviewDAO = new ReviewDAO();
        Review review = new Review();
        review.setCourseID(courseID);
        review.setUserID(userID);
        review.setRate(rate);
        review.setReviewDescription(reviewDescription);
        reviewDAO.submitReview(review); // Assume this method exists
        response.sendRedirect(request.getContextPath() + "/courseDetail?id=" + courseID);
    }
}
