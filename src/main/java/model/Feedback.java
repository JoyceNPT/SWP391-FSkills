package model;

import java.sql.Timestamp;

/**
 * Model class representing user feedback
 */
public class Feedback {
    private int feedbackId;
    private String feedbackType;
    private String feedbackTitle;
    private String feedbackContent;
    private String firstName;
    private String lastName;
    private String email;
    private int userId;
    private Timestamp createdAt;
    private boolean isResolved;

    // Default constructor
    public Feedback() {
    }

    // Constructor with all fields
    public Feedback(int feedbackId, String feedbackType, String feedbackTitle, String feedbackContent, String firstName,
                         String lastName, String email, int userId, Timestamp createdAt, boolean isResolved) {
        this.feedbackId = feedbackId;
        this.feedbackType = feedbackType;
        this.feedbackTitle = feedbackTitle;
        this.feedbackContent = feedbackContent;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
        this.createdAt = createdAt;
        this.isResolved = isResolved;
    }

    // Constructor without ID (for new feedback)
    public Feedback(String feedbackType, String feedbackTitle, String feedbackContent, String firstName,
                         String lastName, String email, int userId) {
        this.feedbackType = feedbackType;
        this.feedbackTitle = feedbackTitle;
        this.feedbackContent = feedbackContent;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.isResolved = false;
    }

    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackTitle() {return feedbackTitle;}

    public void setFeedbackTitle(String feedbackTitle) {this.feedbackTitle = feedbackTitle;}

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Alias for getCreatedAt() to maintain compatibility with Feedback_admin
    public Timestamp getTimestamp() {
        return createdAt;
    }

    public boolean isIsResolved() {
        return isResolved;
    }

    public void setIsResolved(boolean isResolved) {
        this.isResolved = isResolved;
    }

    // Method to get status string from isResolved boolean
    // to maintain compatibility with Feedback_admin
    public String getStatus() {
        return isResolved ? "resolved" : "new";
    }

    // Method to set status from string
    // to maintain compatibility with Feedback_admin
    public void setStatus(String status) {
        this.isResolved = "resolved".equals(status);
    }

    // Add this method to provide compatibility with feedback_admin.jsp
    public String getTitle() {
        return feedbackTitle;
    }

    // Add this method to provide compatibility with feedback_admin.jsp
    public String getContent() {
        return feedbackContent;
    }

    // Add this method to provide compatibility with feedback_admin.jsp
    public String getUserName() {
        return (firstName != null ? firstName : "") + 
               (lastName != null ? " " + lastName : "");
    }

    @Override
    public String toString() {
        return "Feedback{" + "feedbackId=" + feedbackId + ", feedbackType=" + feedbackType + 
               ", feedbackContent=" + feedbackContent + ", firstName=" + firstName + 
               ", lastName=" + lastName + ", email=" + email + ", userId=" + userId + 
               ", createdAt=" + createdAt + ", isResolved=" + isResolved + '}';
    }
}
