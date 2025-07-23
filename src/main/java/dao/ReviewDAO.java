package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Logger;
import model.Review;
import model.User;
import util.DBContext;

public class ReviewDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(ReviewDAO.class.getName());

    public ReviewDAO() {
    }

    public ArrayList<Review> getReviewList(int CourseID) {
        ArrayList<Review> list = new ArrayList<>();
        String sql = "Select * From Reviews Where CourseID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, CourseID);
            ResultSet rs = ps.executeQuery();
            UserDAO uDAO = new UserDAO();
            while (rs.next()) {
                Review review = new Review(rs.getInt("ReviewID"), rs.getInt("UserID"), rs.getInt("CourseID"), 
                                          rs.getFloat("Rate"), rs.getString("ReviewDescription"), uDAO.getByUserID(rs.getInt("UserID")));
                list.add(review);
                LOGGER.info("Loaded review: ReviewID=" + review.getReviewID() + ", Rate=" + review.getRate());
            }
        } catch (Exception e) {
            LOGGER.severe("Error in getReviewList for CourseID " + CourseID + ": " + e.getMessage());
        }
        return list;
    }

    public boolean isReviewed(int UserID, int CourseID) {
        String sql = "Select * From Reviews Where UserID = ? AND CourseID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, UserID);
            ps.setInt(2, CourseID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error in isReviewed for UserID " + UserID + ", CourseID " + CourseID + ": " + e.getMessage());
        }
        return false;
    }

    public int submitReview(Review re) {
        String sql = "Insert Into Reviews (UserID, CourseID, Rate, ReviewDescription) Values (?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, re.getUserID());
            ps.setInt(2, re.getCourseID());
            ps.setFloat(3, re.getRate());
            ps.setString(4, re.getReviewDescription());
            int result = ps.executeUpdate();
            LOGGER.info("Review submitted: UserID=" + re.getUserID() + ", CourseID=" + re.getCourseID() + ", Result=" + result);
            return result;
        } catch (Exception e) {
            LOGGER.severe("Error in submitReview for UserID " + re.getUserID() + ", CourseID " + re.getCourseID() + ": " + e.getMessage());
        }
        return 0;
    }
}