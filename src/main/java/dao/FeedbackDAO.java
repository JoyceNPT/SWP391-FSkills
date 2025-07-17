package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Feedback;
import util.DBContext;

/**
 * Data Access Object for Feedback
 */
public class FeedbackDAO extends DBContext {

    /**
     * Insert a new feedback into the database
     * 
     * @param feedback The feedback to insert
     * @return 1 if successful, 0 if failed
     */
    public int insertFeedback(Feedback feedback) {
        // Sửa đổi SQL để xử lý trường hợp người dùng ẩn danh (userId = 0)
        String sql;
        if (feedback.getUserId() == 0) {
            // Nếu là người dùng ẩn danh, không đưa UserID vào câu lệnh SQL
            sql = "INSERT INTO Feedback (FeedbackType, FeedbackTitle, FeedbackContent, FirstName, LastName, Email, CreatedAt, IsResolved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            // Nếu là người dùng đã đăng nhập, sử dụng câu lệnh SQL ban đầu
            sql = "INSERT INTO Feedback (FeedbackType, FeedbackTitle, FeedbackContent, FirstName, LastName, Email, UserID, CreatedAt, IsResolved) "
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
    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback ORDER BY CreatedAt DESC";
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
                Feedback feedback = new Feedback();
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
    public Feedback getFeedbackById(int feedbackId) {
        String sql = "SELECT * FROM Feedback WHERE FeedbackID = ?";
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
                Feedback feedback = new Feedback();
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
    public List<Feedback> getFeedbackByUserId(int userId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE UserID = ? ORDER BY CreatedAt DESC";
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
                Feedback feedback = new Feedback();
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
        String sql = "UPDATE Feedback SET IsResolved = ? WHERE FeedbackID = ?";
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

    /**
     * Get feedback filtered by type
     * 
     * @param type The type of feedback to retrieve (comments, suggestions, questions)
     * @return List of feedback of the specified type
     */
    public List<Feedback> getFeedbackByType(String type) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE FeedbackType = ? ORDER BY CreatedAt DESC";
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
            ps.setString(1, type);
            rs = ps.executeQuery();

            while (rs.next()) {
                Feedback feedback = new Feedback();
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
            System.err.println("Error getting feedback by type '" + type + "': " + e.getMessage());
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
     * Update the status of a feedback
     * 
     * @param id The ID of the feedback to update
     * @param status The new status (new, read, archived, resolved)
     * @return 1 if successful, 0 if failed
     */
    public int updateFeedbackStatus(int id, String status) {
        String sql = "UPDATE Feedback SET IsResolved = ? WHERE FeedbackID = ?";
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

            // Convert string status to boolean IsResolved
            boolean isResolved = status.equals("resolved");
            ps.setBoolean(1, isResolved);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("Error updating feedback status for ID " + id + " to '" + status + "': " + e.getMessage());
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
     * Delete a feedback permanently from the database
     * 
     * @param id The ID of the feedback to delete
     * @return 1 if successful, 0 if failed
     */
    public int deleteFeedback(int id) {
        String sql = "DELETE FROM Feedback WHERE FeedbackID = ?";
        PreparedStatement ps = null;

        try {
            // Reinitialize connection if needed
            if (conn == null || conn.isClosed()) {
                System.err.println("Attempting to reconnect to database...");
                // Create a new DBContext to get a fresh connection
                DBContext dbContext = new DBContext();
                conn = dbContext.conn;

                if (conn == null || conn.isClosed()) {
                    System.err.println("Failed to reconnect to database");
                    return 0;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            // Log the SQL query for debugging
            System.out.println("Executing SQL: " + sql + " with ID: " + id);

            int result = ps.executeUpdate();
            System.out.println("Delete result: " + result + " rows affected");

            if (result > 0) {
                System.out.println("Successfully deleted feedback with ID: " + id);
                return 1;
            } else {
                System.err.println("No feedback found with ID: " + id);

                // Check if the feedback exists
                String checkSql = "SELECT COUNT(*) FROM Feedback WHERE FeedbackID = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setInt(1, id);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Feedback exists but could not be deleted");
                } else {
                    System.err.println("Feedback with ID " + id + " does not exist in the database");
                }

                rs.close();
                checkPs.close();
                return 0;
            }
        } catch (SQLException e) {
            // Print detailed error information for debugging
            System.err.println("Error deleting feedback with ID " + id + ": " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();

            // Try a simpler approach as a fallback
            try {
                System.out.println("Attempting fallback delete method...");
                String fallbackSql = "DELETE FROM Feedback WHERE FeedbackID = " + id;
                java.sql.Statement stmt = conn.createStatement();
                int fallbackResult = stmt.executeUpdate(fallbackSql);
                stmt.close();

                if (fallbackResult > 0) {
                    System.out.println("Fallback delete method succeeded: " + fallbackResult + " rows affected");
                    return 1;
                } else {
                    System.err.println("Fallback delete method affected 0 rows");
                }
            } catch (SQLException ex) {
                System.err.println("Fallback delete method failed: " + ex.getMessage());
            }
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
     * Delete all feedback records from the database
     * 
     * @return The number of records deleted, or -1 if an error occurred
     */
    public int deleteAllFeedback() {
        String sql = "DELETE FROM Feedback";
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
                    return -1;
                }
                System.out.println("Successfully reconnected to database");
            }

            ps = conn.prepareStatement(sql);

            int result = ps.executeUpdate();
            return result; // Return the actual number of records deleted
        } catch (SQLException e) {
            System.err.println("Error deleting all feedback: " + e.getMessage());
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
        return -1;
    }
}
