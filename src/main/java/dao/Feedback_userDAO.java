package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Feedback_user;
import util.DBContext;

/**
 * Data Access Object for Feedback_user
 */
public class Feedback_userDAO extends DBContext {

    /**
     * Insert a new feedback into the database
     * 
     * @param feedback The feedback to insert
     * @return 1 if successful, 0 if failed
     */
    public int insertFeedback(Feedback_user feedback) {
        // Sửa đổi SQL để xử lý trường hợp người dùng ẩn danh (userId = 0)
        String sql;
        if (feedback.getUserId() == 0) {
            // Nếu là người dùng ẩn danh, không đưa UserID vào câu lệnh SQL
            sql = "INSERT INTO Feedback_user (FeedbackType, FeedbackTitle, FeedbackContent, FirstName, LastName, Email, CreatedAt, IsResolved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            // Nếu là người dùng đã đăng nhập, sử dụng câu lệnh SQL ban đầu
            sql = "INSERT INTO Feedback_user (FeedbackType, FeedbackTitle, FeedbackContent, FirstName, LastName, Email, UserID, CreatedAt, IsResolved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        PreparedStatement ps = null;

        try {
            // Check if connection is valid
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is null or closed");
                // Reinitialize connection
                DBContext dbContext = new DBContext();
                conn = dbContext.conn;

                if (conn == null || conn.isClosed()) {
                    System.err.println("Failed to reconnect to database");
                    return 0;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, feedback.getFeedbackType());
            ps.setString(2, feedback.getFeedbackTitle());
            ps.setString(3, feedback.getFeedbackContent());
            ps.setString(4, feedback.getFirstName());
            ps.setString(5, feedback.getLastName());
            ps.setString(6, feedback.getEmail());

            if (feedback.getUserId() == 0) {
                // Nếu là người dùng ẩn danh, không đặt UserID
                ps.setTimestamp(7, feedback.getCreatedAt() != null ? feedback.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
                ps.setBoolean(8, feedback.isIsResolved());
            } else {
                // Nếu là người dùng đã đăng nhập, đặt UserID
                ps.setInt(7, feedback.getUserId());
                ps.setTimestamp(8, feedback.getCreatedAt() != null ? feedback.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
                ps.setBoolean(9, feedback.isIsResolved());
            }

            // Log the SQL query for debugging
            System.out.println("Executing SQL: " + sql);
            System.out.println("Feedback data: " + feedback.toString());

            int result = ps.executeUpdate();
            System.out.println("Insert result: " + result + " rows affected");

            return result > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("Error inserting feedback: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        } finally {
            // Close resources to prevent leaks
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
        return 0;
    }

    /**
     * Get all feedback from the database
     * 
     * @return List of all feedback
     */
    public List<Feedback_user> getAllFeedback() {
        List<Feedback_user> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback_user ORDER BY CreatedAt DESC";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Check if connection is valid
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is null or closed");
                // Reinitialize connection
                DBContext dbContext = new DBContext();
                conn = dbContext.conn;

                if (conn == null || conn.isClosed()) {
                    System.err.println("Failed to reconnect to database");
                    return feedbackList;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Feedback_user feedback = new Feedback_user();
                feedback.setFeedbackId(rs.getInt("FeedbackID"));
                feedback.setFeedbackType(rs.getString("FeedbackType"));
                feedback.setFeedbackTitle(rs.getString("FeedbackTitle"));
                feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                feedback.setFirstName(rs.getString("FirstName"));
                feedback.setLastName(rs.getString("LastName"));
                feedback.setEmail(rs.getString("Email"));
                feedback.setUserId(rs.getInt("UserID"));
                feedback.setCreatedAt(rs.getTimestamp("CreatedAt"));
                feedback.setIsResolved(rs.getBoolean("IsResolved"));

                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all feedback: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources to prevent leaks
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
        return feedbackList;
    }

    /**
     * Get feedback by ID
     * 
     * @param feedbackId The ID of the feedback to retrieve
     * @return The feedback with the specified ID, or null if not found
     */
    public Feedback_user getFeedbackById(int feedbackId) {
        String sql = "SELECT * FROM Feedback_user WHERE FeedbackID = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Check if connection is valid
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is null or closed");
                // Reinitialize connection
                DBContext dbContext = new DBContext();
                conn = dbContext.conn;

                if (conn == null || conn.isClosed()) {
                    System.err.println("Failed to reconnect to database");
                    return null;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, feedbackId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Feedback_user feedback = new Feedback_user();
                feedback.setFeedbackId(rs.getInt("FeedbackID"));
                feedback.setFeedbackType(rs.getString("FeedbackType"));
                feedback.setFeedbackTitle(rs.getString("FeedbackTitle"));
                feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                feedback.setFirstName(rs.getString("FirstName"));
                feedback.setLastName(rs.getString("LastName"));
                feedback.setEmail(rs.getString("Email"));
                feedback.setUserId(rs.getInt("UserID"));
                feedback.setCreatedAt(rs.getTimestamp("CreatedAt"));
                feedback.setIsResolved(rs.getBoolean("IsResolved"));

                return feedback;
            }
        } catch (SQLException e) {
            System.err.println("Error getting feedback by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources to prevent leaks
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Get feedback by user ID
     * 
     * @param userId The ID of the user whose feedback to retrieve
     * @return List of feedback submitted by the specified user
     */
    public List<Feedback_user> getFeedbackByUserId(int userId) {
        List<Feedback_user> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback_user WHERE UserID = ? ORDER BY CreatedAt DESC";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Check if connection is valid
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is null or closed");
                // Reinitialize connection
                DBContext dbContext = new DBContext();
                conn = dbContext.conn;

                if (conn == null || conn.isClosed()) {
                    System.err.println("Failed to reconnect to database");
                    return feedbackList;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Feedback_user feedback = new Feedback_user();
                feedback.setFeedbackId(rs.getInt("FeedbackID"));
                feedback.setFeedbackType(rs.getString("FeedbackType"));
                feedback.setFeedbackTitle(rs.getString("FeedbackTitle"));
                feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                feedback.setFirstName(rs.getString("FirstName"));
                feedback.setLastName(rs.getString("LastName"));
                feedback.setEmail(rs.getString("Email"));
                feedback.setUserId(rs.getInt("UserID"));
                feedback.setCreatedAt(rs.getTimestamp("CreatedAt"));
                feedback.setIsResolved(rs.getBoolean("IsResolved"));

                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            System.err.println("Error getting feedback by user ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources to prevent leaks
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
        return feedbackList;
    }

    /**
     * Update the resolved status of a feedback
     * 
     * @param feedbackId The ID of the feedback to update
     * @param isResolved The new resolved status
     * @return 1 if successful, 0 if failed
     */
    public int updateFeedbackStatus(int feedbackId, boolean isResolved) {
        String sql = "UPDATE Feedback_user SET IsResolved = ? WHERE FeedbackID = ?";
        PreparedStatement ps = null;

        try {
            // Check if connection is valid
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is null or closed");
                // Reinitialize connection
                DBContext dbContext = new DBContext();
                conn = dbContext.conn;

                if (conn == null || conn.isClosed()) {
                    System.err.println("Failed to reconnect to database");
                    return 0;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isResolved);
            ps.setInt(2, feedbackId);

            // Log the SQL query for debugging
            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: isResolved=" + isResolved + ", feedbackId=" + feedbackId);

            int result = ps.executeUpdate();
            System.out.println("Update result: " + result + " rows affected");

            return result > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("Error updating feedback status: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        } finally {
            // Close resources to prevent leaks
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
        return 0;
    }
}
