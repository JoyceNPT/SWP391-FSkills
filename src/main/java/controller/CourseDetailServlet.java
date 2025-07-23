package controller;

import dao.CartDAO;
import dao.CourseDAO;
import dao.EnrollDAO;
import dao.ReviewDAO;
import dao.StudyDAO;
import model.Course;
import model.Review;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/courseDetail")
public class CourseDetailServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CourseDetailServlet.class.getName());
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

            // Get review list
            List<Review> reviewList = reviewDAO.getReviewList(courseId);
            LOGGER.info("Review list size for courseID " + courseId + ": " + reviewList.size());

            // Calculate average rating and rating counts
            double averageRating = 0.0;
            Map<Integer, Integer> ratingCounts = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                ratingCounts.put(i, 0);
            }

            if (!reviewList.isEmpty()) {
                double totalRating = 0.0;
                for (Review review : reviewList) {
                    int rating = (int) review.getRate();
                    if (rating >= 1 && rating <= 5) {
                        totalRating += review.getRate();
                        ratingCounts.put(rating, ratingCounts.getOrDefault(rating, 0) + 1);
                        LOGGER.info("Review: UserID=" + review.getUserID() + ", Rate=" + review.getRate());
                    } else {
                        LOGGER.warning("Invalid rating found: " + review.getRate());
                    }
                }
                averageRating = totalRating / reviewList.size();
            } else {
                LOGGER.warning("No reviews found for courseID: " + courseId);
            }

            // Set attributes for JSP
            request.setAttribute("course", course);
            request.setAttribute("isInCart", cartDAO.isInCart(user.getUserId(), course.getCourseID()));
            request.setAttribute("isBought", enrollDAO.checkBought(user.getUserId(), course.getCourseID()));
            request.setAttribute("isEnroll", enrollDAO.checkEnrollment(user.getUserId(), course.getCourseID()));
            request.setAttribute("reviewList", reviewList);
            request.setAttribute("studyProgress", studyDAO.returnStudyProgress(user.getUserId(), course.getCourseID()));
            request.setAttribute("hasReviewed", reviewDAO.isReviewed(user.getUserId(), course.getCourseID()));
            request.setAttribute("averageRating", averageRating); // Set as double
            request.setAttribute("ratingCounts", ratingCounts);
            request.setAttribute("totalReviews", reviewList.size());

            // Forward to course details JSP
            request.getRequestDispatcher("/WEB-INF/views/courseDetailsLearner.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.severe("Invalid course ID: " + courseIdParam);
            response.sendRedirect("AllCourses");
        } catch (Exception e) {
            LOGGER.severe("Error processing request: " + e.getMessage());
            throw new ServletException("Error processing course details", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int courseID = Integer.parseInt(request.getParameter("courseID"));
            int userID = Integer.parseInt(request.getParameter("userID"));
            String rateParam = request.getParameter("rate");
            String reviewDescription = request.getParameter("reviewDescription");

            // Validate rating
            float rate;
            try {
                rate = Float.parseFloat(rateParam);
                if (rate < 1 || rate > 5 || rate != Math.floor(rate)) {
                    request.setAttribute("error", "Rating must be an integer between 1 and 5.");
                    doGet(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid rating format.");
                doGet(request, response);
                return;
            }

            // Validate review description
            if (reviewDescription == null || reviewDescription.trim().isEmpty()) {
                request.setAttribute("error", "Review description is required.");
                doGet(request, response);
                return;
            }

            Review review = new Review();
            review.setCourseID(courseID);
            review.setUserID(userID);
            review.setRate(rate);
            review.setReviewDescription(reviewDescription.trim());
            int result = reviewDAO.submitReview(review);
            LOGGER.info("Review submission result: " + result);

            response.sendRedirect(request.getContextPath() + "/courseDetail?id=" + courseID);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input format.");
            doGet(request, response);
        }
    }
}