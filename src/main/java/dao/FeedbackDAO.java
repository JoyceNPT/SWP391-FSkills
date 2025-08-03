package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Feedback;
import util.DBContext;

public class FeedbackDAO extends DBContext {

    private boolean ensureConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = new DBContext().conn;
            }
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection check failed: " + e.getMessage());
            return false;
        }
    }

    public int insertFeedback(Feedback feedback) {
        if (!ensureConnection()) return 0;

        boolean isAnonymous = feedback.getUserId() == 0;
        String sql = isAnonymous
                ? "INSERT INTO Feedback (FeedbackType, FeedbackTitle, FeedbackContent, FirstName, LastName, Email, CreatedAt, IsResolved) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                : "INSERT INTO Feedback (FeedbackType, FeedbackTitle, FeedbackContent, FirstName, LastName, Email, UserID, CreatedAt, IsResolved) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, feedback.getFeedbackType());
            ps.setString(2, feedback.getFeedbackTitle());
            ps.setString(3, feedback.getFeedbackContent());
            ps.setString(4, feedback.getFirstName());
            ps.setString(5, feedback.getLastName());
            ps.setString(6, feedback.getEmail());

            int index = 7;
            if (!isAnonymous) ps.setInt(index++, feedback.getUserId());
            ps.setTimestamp(index++, feedback.getCreatedAt() != null
                    ? feedback.getCreatedAt()
                    : new Timestamp(System.currentTimeMillis()));
            ps.setBoolean(index, feedback.isIsResolved());

            return ps.executeUpdate() > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("Insert feedback failed: " + e.getMessage());
            return 0;
        }
    }

    public List<Feedback> getAllFeedback() {
        return getFeedbackByQuery("SELECT * FROM Feedback ORDER BY CreatedAt DESC", null);
    }

    public List<Feedback> getFeedbackByType(String type) {
        return getFeedbackByQuery("SELECT * FROM Feedback WHERE FeedbackType = ? ORDER BY CreatedAt DESC", ps -> {
            ps.setString(1, type);
        });
    }

    public List<Feedback> getFeedbackByUserId(int userId) {
        return getFeedbackByQuery("SELECT * FROM Feedback WHERE UserID = ? ORDER BY CreatedAt DESC", ps -> {
            ps.setInt(1, userId);
        });
    }

    public Feedback getFeedbackById(int id) {
        List<Feedback> list = getFeedbackByQuery("SELECT * FROM Feedback WHERE FeedbackID = ?", ps -> {
            ps.setInt(1, id);
        });
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Feedback> getFeedbackByQuery(String sql, SQLConsumer<PreparedStatement> paramSetter) {
        List<Feedback> list = new ArrayList<>();
        if (!ensureConnection()) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (paramSetter != null) paramSetter.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFeedback(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }

        return list;
    }

    public int updateFeedbackStatus(int id, boolean isResolved) {
        if (!ensureConnection()) return 0;

        String sql = "UPDATE Feedback SET IsResolved = ? WHERE FeedbackID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isResolved);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("Update status failed: " + e.getMessage());
            return 0;
        }
    }

    public int updateFeedbackStatus(int id, String status) {
        return updateFeedbackStatus(id, "resolved".equalsIgnoreCase(status));
    }

    public int deleteFeedback(int id) {
        return executeUpdate("DELETE FROM Feedback WHERE FeedbackID = ?", ps -> ps.setInt(1, id));
    }

    public int deleteAllFeedback() {
        return executeUpdate("DELETE FROM Feedback", null);
    }

    private int executeUpdate(String sql, SQLConsumer<PreparedStatement> paramSetter) {
        if (!ensureConnection()) return -1;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (paramSetter != null) paramSetter.accept(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Execute update failed: " + e.getMessage());
            return -1;
        }
    }

    private Feedback extractFeedback(ResultSet rs) throws SQLException {
        Feedback fb = new Feedback();
        fb.setFeedbackId(rs.getInt("FeedbackID"));
        fb.setFeedbackType(rs.getString("FeedbackType"));
        fb.setFeedbackTitle(rs.getString("FeedbackTitle"));
        fb.setFeedbackContent(rs.getString("FeedbackContent"));
        fb.setFirstName(rs.getString("FirstName"));
        fb.setLastName(rs.getString("LastName"));
        fb.setEmail(rs.getString("Email"));
        fb.setUserId(rs.getInt("UserID"));
        fb.setCreatedAt(rs.getTimestamp("CreatedAt"));
        fb.setIsResolved(rs.getBoolean("IsResolved"));
        return fb;
    }

    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
