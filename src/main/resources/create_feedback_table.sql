-- SQL script to create the Feedback table
-- This table stores user feedback with various attributes

CREATE TABLE Feedback (
    FeedbackID INT PRIMARY KEY IDENTITY(1,1),
    FeedbackType NVARCHAR(50) NOT NULL,
    FeedbackTitle NVARCHAR(255) NOT NULL,
    FeedbackContent NVARCHAR(MAX) NOT NULL,
    FirstName NVARCHAR(100),
    LastName NVARCHAR(100),
    Email NVARCHAR(255),
    UserID INT,
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsResolved BIT DEFAULT 0
);
