/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://github/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.security.MessageDigest;
import java.security.SecureRandom;

import model.*;
import util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import model.UserGoogle;

/**
 *
 * @author Duy
 * @author Ngo Phuoc Thinh - CE170008 - SE1815
 */
public class UserDAO extends DBContext {

    //sinh viên google
    public List<User> getAllStudentsWithAvatar() {
        List<User> list = new ArrayList<>();
        String sql = "select UserID, UserName, DisplayName, Role, BanStatus, ReportAmount, AvatarGoogle from Users Order by ReportAmount Desc";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setUserName(rs.getString("UserName"));
                u.setDisplayName(rs.getString("DisplayName"));
                int roleInt = rs.getInt("Role");
                switch (roleInt) {
                    case 0:
                        u.setRole(Role.LEARNER);
                        break;
                    case 1:
                        u.setRole(Role.INSTRUCTOR);
                        break;
                    case 2:
                        u.setRole(Role.ADMIN);
                        break;
                }
                int banInt = rs.getInt("BanStatus");
                switch (banInt) {
                    case 0:
                        u.setBan(Ban.NORMAL);
                        break;
                    case 1:
                        u.setBan(Ban.BANNED);
                        break;
                }
                u.setReports(rs.getInt("ReportAmount"));
                u.setAvatarUrl(rs.getString("AvatarGoogle"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllLearnersWithAvatar() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role, BanStatus, ReportAmount, AvatarGoogle FROM Users WHERE Role = ? ORDER BY ReportAmount DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Role.LEARNER.ordinal());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setUserName(rs.getString("UserName"));
                u.setDisplayName(rs.getString("DisplayName"));
                u.setRole(Role.LEARNER);
                int banInt = rs.getInt("BanStatus");
                switch (banInt) {
                    case 0:
                        u.setBan(Ban.NORMAL);
                        break;
                    case 1:
                        u.setBan(Ban.BANNED);
                        break;
                }
                u.setReports(rs.getInt("ReportAmount"));
                u.setAvatarUrl(rs.getString("AvatarGoogle"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllInstructorsWithAvatar() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role, BanStatus, ReportAmount, AvatarGoogle FROM Users WHERE Role = ? ORDER BY ReportAmount DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Role.INSTRUCTOR.ordinal());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setUserName(rs.getString("UserName"));
                u.setDisplayName(rs.getString("DisplayName"));
                u.setRole(Role.INSTRUCTOR);
                int banInt = rs.getInt("BanStatus");
                switch (banInt) {
                    case 0:
                        u.setBan(Ban.NORMAL);
                        break;
                    case 1:
                        u.setBan(Ban.BANNED);
                        break;
                }
                u.setReports(rs.getInt("ReportAmount"));
                u.setAvatarUrl(rs.getString("AvatarGoogle"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> searchUsersByNameWithAvatar(String searchName) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role, BanStatus, ReportAmount, AvatarGoogle FROM Users WHERE LOWER(UserName) LIKE LOWER(?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchName + "%");
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUserName(rs.getString("UserName"));
                    u.setDisplayName(rs.getString("DisplayName"));
                    int roleInt = rs.getInt("Role");
                    switch (roleInt) {
                        case 0:
                            u.setRole(Role.LEARNER);
                            break;
                        case 1:
                            u.setRole(Role.INSTRUCTOR);
                            break;
                        case 2:
                            u.setRole(Role.ADMIN);
                            break;
                    }
                    int banInt = rs.getInt("BanStatus");
                    switch (banInt) {
                        case 0:
                            u.setBan(Ban.NORMAL);
                            break;
                        case 1:
                            u.setBan(Ban.BANNED);
                            break;
                    }
                    u.setReports(rs.getInt("ReportAmount"));
                    u.setAvatarUrl(rs.getString("AvatarGoogle"));
                }
            }
        }
        return users;
    }

    public List<User> searchLearnersByNameWithAvatar(String searchName) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role, BanStatus, ReportAmount, AvatarGoogle FROM Users WHERE Role = ? AND LOWER(UserName) LIKE LOWER(?) ORDER BY ReportAmount DESC";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Role.LEARNER.ordinal());
            ps.setString(2, "%" + searchName + "%");
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUserName(rs.getString("UserName"));
                    u.setDisplayName(rs.getString("DisplayName"));
                    u.setRole(Role.LEARNER);
                    int banInt = rs.getInt("BanStatus");
                    switch (banInt) {
                        case 0:
                            u.setBan(Ban.NORMAL);
                            break;
                        case 1:
                            u.setBan(Ban.BANNED);
                            break;
                    }
                    u.setReports(rs.getInt("ReportAmount"));
                    u.setAvatarUrl(rs.getString("AvatarGoogle"));
                    users.add(u);
                }
            }
        }
        return users;
    }

    public List<User> searchInstructorsByNameWithAvatar(String searchName) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role, BanStatus, ReportAmount, AvatarGoogle FROM Users WHERE Role = ? AND LOWER(UserName) LIKE LOWER(?) ORDER BY ReportAmount DESC";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Role.INSTRUCTOR.ordinal());
            ps.setString(2, "%" + searchName + "%");
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUserName(rs.getString("UserName"));
                    u.setDisplayName(rs.getString("DisplayName"));
                    u.setRole(Role.INSTRUCTOR);
                    int banInt = rs.getInt("BanStatus");
                    switch (banInt) {
                        case 0:
                            u.setBan(Ban.NORMAL);
                            break;
                        case 1:
                            u.setBan(Ban.BANNED);
                            break;
                    }
                    u.setReports(rs.getInt("ReportAmount"));
                    u.setAvatarUrl(rs.getString("AvatarGoogle"));
                    users.add(u);
                }
            }
        }
        return users;
    }

    public List<User> showAllInformWithAvatar(String informUser) throws SQLException {
        List<User> us = new ArrayList<>();
        String sql = "SELECT UserName, DisplayName, Email, Password, Role, DateOfBirth, UserCreateDate, info, BanStatus, ReportAmount, PhoneNumber, AvatarGoogle FROM Users WHERE UserName = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, informUser);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserName(rs.getString("UserName"));
            u.setDisplayName(rs.getString("DisplayName"));
            u.setEmail(rs.getString("Email"));
            u.setPassword(rs.getString("Password"));
            int roleInt = rs.getInt("Role");
            switch (roleInt) {
                case 0:
                    u.setRole(Role.LEARNER);
                    break;
                case 1:
                    u.setRole(Role.INSTRUCTOR);
                    break;
                case 2:
                    u.setRole(Role.ADMIN);
                    break;
            }
            u.setDateOfBirth(rs.getTimestamp("DateOfBirth"));
            u.setUserCreateDate(rs.getTimestamp("UserCreateDate"));
            u.setInfo(rs.getString("info"));
            int banInt = rs.getInt("BanStatus");
            switch (banInt) {
                case 0:
                    u.setBan(Ban.NORMAL);
                    break;
                case 1:
                    u.setBan(Ban.BANNED);
                    break;
            }
            u.setPhone(rs.getString("PhoneNumber"));
            u.setReports(rs.getInt("ReportAmount"));
            u.setAvatarUrl(rs.getString("AvatarGoogle")); // <-- ĐÃ THÊM DÒNG NÀY CHO PHƯƠNG THỨC MỚI
            us.add(u);
        }
        return us;
    }

    public User verifyMD5WithAvatar(String input, String Password) {
        String sql = "SELECT *, AvatarGoogle FROM Users WHERE (UserName = ? OR Email = ?) AND Password = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, input);
            ps.setString(2, input);
            ps.setString(3, hashMD5(Password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Email = rs.getString("Email");
                Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");
                String avatarGoogleUrl = rs.getString("AvatarGoogle");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, GoogleID);
                acc.setAvatarUrl(avatarGoogleUrl);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User getByUsernameWithAvatar(String userName) {
        String sql = "SELECT *, AvatarGoogle FROM users WHERE UserName = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUserName(rs.getString("UserName"));
                user.setDisplayName(rs.getNString("DisplayName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                int roleInt = rs.getInt("Role");
                switch (roleInt) {
                    case 0:
                        user.setRole(Role.LEARNER);
                        break;
                    case 1:
                        user.setRole(Role.INSTRUCTOR);
                        break;
                    case 2:
                        user.setRole(Role.ADMIN);
                        break;
                }
                user.setGender(rs.getInt("Gender"));
                user.setDateOfBirth(rs.getTimestamp("DateOfBirth"));
                user.setUserCreateDate(rs.getTimestamp("UserCreateDate"));
                user.setAvatar(rs.getBytes("Avatar"));
                user.setInfo(rs.getNString("Info"));
                user.setReports(rs.getInt("ReportAmount"));
                user.setPhone(rs.getString("PhoneNumber"));
                user.setIsVerified(rs.getBoolean("IsVerified"));
                int banInt = rs.getInt("BanStatus");
                switch (banInt) {
                    case 0:
                        user.setBan(Ban.NORMAL);
                        break;
                    case 1:
                        user.setBan(Ban.BANNED);
                        break;
                }
                user.setGoogleID(rs.getString("GoogleID"));
                user.setAvatarUrl(rs.getString("AvatarGoogle"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User getByUserIDWithAvatar(int userID) {
        String sql = "SELECT *, AvatarGoogle FROM Users WHERE UserID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("UserName");
                String displayName = rs.getString("DisplayName");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp birthOfDate = rs.getTimestamp("DateOfBirth");
                Timestamp timeCreate = rs.getTimestamp("UserCreateDate");
                byte[] avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int reportAmount = rs.getInt("ReportAmount");
                String phoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");
                String avatarGoogleUrl = rs.getString("AvatarGoogle");

                User acc = new User(userID, userName, displayName, email, password, role, gender, birthOfDate, timeCreate, avatar, info, Ban, reportAmount, phoneNumber, isVerified, GoogleID);
                acc.setAvatarUrl(avatarGoogleUrl);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User findByGoogleIDWithAvatar(String googleID) {
        String sql = "SELECT *, AvatarGoogle FROM Users WHERE GoogleID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, googleID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Email = rs.getString("Email");
                String Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar"); // Avatar thường (byte[])
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String avatarGoogleUrl = rs.getString("AvatarGoogle");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, googleID);
                acc.setAvatarUrl(avatarGoogleUrl);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // NEW METHOD: Tìm người dùng theo Email với avatar Google
    public User findByEmailWithAvatar(String Email) {
        String sql = "SELECT *, AvatarGoogle FROM Users WHERE Email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");
                String avatarGoogleUrl = rs.getString("AvatarGoogle");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, GoogleID);
                acc.setAvatarUrl(avatarGoogleUrl);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User findByTokenWithAvatar(String token) {
        String sql = "SELECT u.*, u.AvatarGoogle FROM users u JOIN RememberTokens t ON u.UserID = t.user_id WHERE t.token = ? AND t.expiry_date > CURRENT_TIMESTAMP;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Email = rs.getString("Email");
                String Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar"); // Avatar thường (byte[])
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");
                String avatarGoogleUrl = rs.getString("AvatarGoogle");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, GoogleID);
                acc.setAvatarUrl(avatarGoogleUrl);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean checkPassword(int userId, String oldPassword) throws Exception {
        String sql = "SELECT COUNT(*) FROM Users WHERE userId = ? AND password = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, hashMD5(oldPassword));
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void updatePassword(int userId, String newPassword) throws Exception {
        String sql = "UPDATE Users SET password = ? WHERE userId = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashMD5(newPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public List<User> getAllStudents() { //hien tai chua dung
        List<User> list = new ArrayList<>();
        String sql = "select UserID, UserName, DisplayName, Role, BanStatus, ReportAmount from Users Order by ReportAmount Desc";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setUserName(rs.getString("UserName"));
                u.setDisplayName(rs.getString("DisplayName"));
                int roleInt = rs.getInt("Role");
                switch (roleInt) {
                    case 0:
                        u.setRole(Role.LEARNER);
                        break;
                    case 1:
                        u.setRole(Role.INSTRUCTOR);
                        break;
                    case 2:
                        u.setRole(Role.ADMIN);
                        break;
                }
                int banInt = rs.getInt("BanStatus");
                switch (banInt) {
                    case 0:
                        u.setBan(Ban.NORMAL);
                        break;
                    case 1:
                        u.setBan(Ban.BANNED);
                        break;
                }
                u.setReports(rs.getInt("ReportAmount"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllLearners() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.UserName, u.DisplayName, u.BanStatus, "
                + "(SELECT COUNT(*) FROM Reports r "
                + " WHERE ( "
                + "     (r.CommentID IS NOT NULL AND EXISTS ( "
                + "         SELECT 1 FROM Comments c WHERE r.CommentID = c.CommentID AND c.UserID = u.UserID)) "
                + "     OR "
                + "     (r.CommentID IS NULL AND EXISTS ( "
                + "         SELECT 1 FROM Materials m "
                + "         JOIN Modules mo ON m.ModuleID = mo.ModuleID "
                + "         JOIN Courses co ON mo.CourseID = co.CourseID "
                + "         WHERE m.MaterialID = r.MaterialID AND co.UserID = u.UserID)) "
                + ")) AS ReportCount "
                + "FROM Users u WHERE u.Role = 0";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserId(rs.getInt("UserID"));
            u.setUserName(rs.getString("UserName"));
            u.setDisplayName(rs.getString("DisplayName"));
            u.setRole(Role.LEARNER);
            int ban = rs.getInt("BanStatus");
            u.setBan(ban == 1 ? Ban.BANNED : Ban.NORMAL);
            u.setReports(rs.getInt("ReportCount"));
            list.add(u);
        }
        return list;
    }

    public List<User> getAllInstructors() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.UserName, u.DisplayName, u.BanStatus, "
                + "(SELECT COUNT(*) FROM Reports r "
                + " WHERE ( "
                + "     (r.CommentID IS NOT NULL AND EXISTS ( "
                + "         SELECT 1 FROM Comments c WHERE r.CommentID = c.CommentID AND c.UserID = u.UserID)) "
                + "     OR "
                + "     (r.CommentID IS NULL AND EXISTS ( "
                + "         SELECT 1 FROM Materials m "
                + "         JOIN Modules mo ON m.ModuleID = mo.ModuleID "
                + "         JOIN Courses co ON mo.CourseID = co.CourseID "
                + "         WHERE m.MaterialID = r.MaterialID AND co.UserID = u.UserID)) "
                + ")) AS ReportCount "
                + "FROM Users u WHERE u.Role = 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserId(rs.getInt("UserID"));
            u.setUserName(rs.getString("UserName"));
            u.setDisplayName(rs.getString("DisplayName"));
            u.setRole(Role.INSTRUCTOR);
            int ban = rs.getInt("BanStatus");
            u.setBan(ban == 1 ? Ban.BANNED : Ban.NORMAL);
            u.setReports(rs.getInt("ReportCount"));
            list.add(u);
        }
        return list;
    }


    public List<User> searchUsersByName(String searchName) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role, BanStatus, ReportAmount FROM Users WHERE LOWER(UserName) LIKE LOWER(?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchName + "%");
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUserName(rs.getString("UserName"));
                    u.setDisplayName(rs.getString("DisplayName"));
                    int roleInt = rs.getInt("Role");
                    switch (roleInt) {
                        case 0:
                            u.setRole(Role.LEARNER);
                            break;
                        case 1:
                            u.setRole(Role.INSTRUCTOR);
                            break;
                        case 2:
                            u.setRole(Role.ADMIN);
                            break;
                    }
                    int banInt = rs.getInt("BanStatus");
                    switch (banInt) {
                        case 0:
                            u.setBan(Ban.NORMAL);
                            break;
                        case 1:
                            u.setBan(Ban.BANNED);
                            break;
                    }
                    u.setReports(rs.getInt("ReportAmount"));
                    users.add(u);
                }
            }
        }
        return users;
    }

    public List<User> searchLearnersByName(String name) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.UserName, u.DisplayName, u.BanStatus, "
                + "(SELECT COUNT(*) FROM Reports r "
                + " WHERE ( "
                + "     (r.CommentID IS NOT NULL AND EXISTS ( "
                + "         SELECT 1 FROM Comments c WHERE r.CommentID = c.CommentID AND c.UserID = u.UserID)) "
                + "     OR "
                + "     (r.CommentID IS NULL AND EXISTS ( "
                + "         SELECT 1 FROM Materials m "
                + "         JOIN Modules mo ON m.ModuleID = mo.ModuleID "
                + "         JOIN Courses co ON mo.CourseID = co.CourseID "
                + "         WHERE m.MaterialID = r.MaterialID AND co.UserID = u.UserID)) "
                + ")) AS ReportCount "
                + "FROM Users u WHERE u.Role = 0 AND u.UserName LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + name + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserId(rs.getInt("UserID"));
            u.setUserName(rs.getString("UserName"));
            u.setDisplayName(rs.getString("DisplayName"));
            u.setRole(Role.LEARNER);
            int ban = rs.getInt("BanStatus");
            u.setBan(ban == 1 ? Ban.BANNED : Ban.NORMAL);
            u.setReports(rs.getInt("ReportCount"));
            list.add(u);
        }
        return list;
    }

    public List<User> searchInstructorsByName(String name) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.UserName, u.DisplayName, u.BanStatus, "
                + "(SELECT COUNT(*) FROM Reports r "
                + " WHERE ( "
                + "     (r.CommentID IS NOT NULL AND EXISTS ( "
                + "         SELECT 1 FROM Comments c WHERE r.CommentID = c.CommentID AND c.UserID = u.UserID)) "
                + "     OR "
                + "     (r.CommentID IS NULL AND EXISTS ( "
                + "         SELECT 1 FROM Materials m "
                + "         JOIN Modules mo ON m.ModuleID = mo.ModuleID "
                + "         JOIN Courses co ON mo.CourseID = co.CourseID "
                + "         WHERE m.MaterialID = r.MaterialID AND co.UserID = u.UserID)) "
                + ")) AS ReportCount "
                + "FROM Users u WHERE u.Role = 1 AND u.UserName LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + name + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserId(rs.getInt("UserID"));
            u.setUserName(rs.getString("UserName"));
            u.setDisplayName(rs.getString("DisplayName"));
            u.setRole(Role.INSTRUCTOR);
            int ban = rs.getInt("BanStatus");
            u.setBan(ban == 1 ? Ban.BANNED : Ban.NORMAL);
            u.setReports(rs.getInt("ReportCount"));
            list.add(u);
        }
        return list;
    }


    public boolean deleteAccount(String userName) throws SQLException {
        String sql = "DELETE FROM Users WHERE UserName = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<User> showAllInform(String informUser) throws SQLException {
        List<User> us = new ArrayList<>();
        String sql = "SELECT u.UserID, u.UserName, u.DisplayName, u.Email, u.Password, u.Role, "
                + "u.DateOfBirth, u.UserCreateDate, u.info, u.BanStatus, u.PhoneNumber, "
                + "u.Avatar, u.AvatarGoogle, "
                + "(SELECT COUNT(*) FROM Reports r "
                + " WHERE ( "
                + "     (r.CommentID IS NOT NULL AND EXISTS ( "
                + "         SELECT 1 FROM Comments c WHERE r.CommentID = c.CommentID AND c.UserID = u.UserID)) "
                + "     OR "
                + "     (r.CommentID IS NULL AND EXISTS ( "
                + "         SELECT 1 FROM Materials m "
                + "         JOIN Modules mo ON m.ModuleID = mo.ModuleID "
                + "         JOIN Courses co ON mo.CourseID = co.CourseID "
                + "         WHERE m.MaterialID = r.MaterialID AND co.UserID = u.UserID)) "
                + ")) AS ReportCount "
                + "FROM Users u WHERE u.UserName = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, informUser);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserId(rs.getInt("UserID"));
            u.setUserName(rs.getString("UserName"));
            u.setDisplayName(rs.getString("DisplayName"));
            u.setEmail(rs.getString("Email"));
            u.setPassword(rs.getString("Password"));

            int roleInt = rs.getInt("Role");
            switch (roleInt) {
                case 0:
                    u.setRole(Role.LEARNER);
                    break;
                case 1:
                    u.setRole(Role.INSTRUCTOR);
                    break;
                case 2:
                    u.setRole(Role.ADMIN);
                    break;
            }

            u.setDateOfBirth(rs.getTimestamp("DateOfBirth"));
            u.setUserCreateDate(rs.getTimestamp("UserCreateDate"));
            u.setInfo(rs.getString("info"));

            int banInt = rs.getInt("BanStatus");
            switch (banInt) {
                case 0:
                    u.setBan(Ban.NORMAL);
                    break;
                case 1:
                    u.setBan(Ban.BANNED);
                    break;
            }

            u.setPhone(rs.getString("PhoneNumber"));
            u.setReports(rs.getInt("ReportCount"));
            u.setAvatar(rs.getBytes("Avatar"));
            u.setAvatarUrl(rs.getString("AvatarGoogle"));
            us.add(u);
        }
        return us;
    }



    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET DisplayName = ?, Email = ?, Role = ?, BanStatus = ?, ReportAmount = ?, DateOfBirth = ?, info = ?, PhoneNumber = ? WHERE UserName = ?";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1; //dùng i thì ít gây ra lôi null hon
            ps.setString(i++, user.getDisplayName());
            ps.setString(i++, user.getEmail());
            if (user.getRole() != null) {
                ps.setInt(i++, user.getRole().ordinal());
            }
            if (user.getBan() != null) {
                ps.setInt(i++, user.getBan().ordinal());
            }
            ps.setInt(i++, user.getReports());
            ps.setTimestamp(i++, user.getDateOfBirth());
            ps.setString(i++, user.getInfo());
            ps.setString(i++, user.getPhone());
            ps.setString(i++, user.getUserName());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public String hashMD5(String pass) {
        try {
            MessageDigest mes = MessageDigest.getInstance("MD5");
            byte[] mesMD5 = mes.digest(pass.getBytes());
            StringBuilder str = new StringBuilder();
            for (byte b : mesMD5) {
                String ch = String.format("%02x", b);
                str.append(ch);
            }
            return str.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public boolean banAccount(String userName) throws SQLException {
        String sql = "UPDATE Users SET BanStatus = CASE WHEN BanStatus = 0 THEN 1 WHEN BanStatus = 1 THEN 0 ELSE BanStatus END WHERE UserName = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            int ii = ps.executeUpdate();
            return ii > 0;
        }
    }

    public User verifyMD5(String input, String Password) {
        String sql = "SELECT * FROM Users WHERE (UserName = ? OR Email = ?) AND Password = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, input); // có thể là username hoặc email
            ps.setString(2, input);
            ps.setString(3, hashMD5(Password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Email = rs.getString("Email");
                Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, GoogleID);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int insertUser(User user) {
        String hashMD5 = hashMD5(user.getPassword());

        String sql = "INSERT INTO users (UserName, DisplayName, Email, Password, Role, PhoneNumber, IsVerified, BanStatus) VALUES (?, 'Newbie', ?, ?, 0, ?, 0, 0)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashMD5);
            ps.setString(4, user.getPhone());

            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public User getByUsername(String userName) {
        String sql = "SELECT * FROM users WHERE UserName = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUserName(rs.getString("UserName"));
                user.setDisplayName(rs.getNString("DisplayName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                int roleInt = rs.getInt("Role");
                switch (roleInt) {
                    case 0:
                        user.setRole(Role.LEARNER);
                        break;
                    case 1:
                        user.setRole(Role.INSTRUCTOR);
                        break;
                    case 2:
                        user.setRole(Role.ADMIN);
                        break;
                }
                user.setGender(rs.getInt("Gender"));
                user.setDateOfBirth(rs.getTimestamp("DateOfBirth"));
                user.setUserCreateDate(rs.getTimestamp("UserCreateDate"));
                user.setAvatar(rs.getBytes("Avatar"));
                user.setInfo(rs.getNString("Info"));
                user.setReports(rs.getInt("ReportAmount"));
                user.setPhone(rs.getString("PhoneNumber"));
                user.setIsVerified(rs.getBoolean("IsVerified"));
                int banInt = rs.getInt("BanStatus");
                switch (banInt) {
                    case 0:
                        user.setBan(Ban.NORMAL);
                        break;
                    case 1:
                        user.setBan(Ban.BANNED);
                        break;
                }

                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User getByUserID(int userID) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String userName = rs.getString("UserName");
                String displayName = rs.getString("DisplayName");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp birthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp timeCreate = rs.getTimestamp("UserCreateDate");
                byte[] avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int reportAmount = rs.getInt("ReportAmount");
                String phoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");

                User acc = new User(userID, userName, displayName, email, password, role, gender, birthOfDay, timeCreate, avatar, info, Ban, reportAmount, info, isVerified, GoogleID);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User findByGoogleID(String googleID) {
        String sql = "SELECT * FROM Users WHERE GoogleID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, googleID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Email = rs.getString("Email");
                String Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, googleID);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User findByEmail(String Email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");
                String avatarGoogleUrl = rs.getString("AvatarGoogle");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, BirthOfDay, TimeCreate, Avatar, info, Ban, ReportAmount, PhoneNumber, isVerified, GoogleID, avatarGoogleUrl);

                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int updateGoogleID(User user) {
        String sql = "UPDATE Users SET GoogleID = ?, IsVerified = ?, AvatarGoogle = ? WHERE UserID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getGoogleID());
            ps.setBoolean(2, true);
            ps.setString(3, user.getAvatarUrl());
            ps.setInt(4, user.getUserId());

            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int insertGoogle(UserGoogle user) {
        String sql = "INSERT INTO Users (UserName, DisplayName, Email, Password, Role, AvatarGoogle, GoogleID, IsVerified) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            String email = user.getEmail();
            String name = user.getName();
            String picture = user.getPicture();
            String googleID = user.getId();

            String username = email.split("@")[0];
            String password = generateRandomPassword(10);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setInt(5, 0);
            ps.setString(6, picture);
            ps.setString(7, googleID);
            ps.setBoolean(8, true);

            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;

        } catch (Exception e) {
            System.out.println("insertGoogle error: " + e.getMessage());
        }
        return 0;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public void saveTokenVerifyEmail(int userId, String token, Timestamp createdAt, Timestamp expiresAt) {
        String sql = "INSERT INTO VerifyEmailTokens (UserID, token, created_at, expires_at) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, createdAt);
            ps.setTimestamp(4, expiresAt);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public VerifyEmailToken findValidTokenVerifyEmail(String token) {
        String sql = "SELECT * FROM VerifyEmailTokens WHERE token = ? AND expires_at > GETUTCDATE()";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                VerifyEmailToken vet = new VerifyEmailToken();
                vet.setId(rs.getInt("id"));
                vet.setUserId(rs.getInt("UserID"));
                vet.setToken(token);
                return vet;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteTokenVerifyEmail(int userId) {
        String sql = "DELETE FROM VerifyEmailTokens WHERE UserID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int updateIsVerified(int userId) {
        String sql = "UPDATE Users SET IsVerified = 1 WHERE UserID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void saveTokenForgotPassword(int userId, String token, Timestamp createdAt, Timestamp expiresAt) {
        String sql = "INSERT INTO PasswordResetTokens (UserID, token, created_at, expires_at) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, createdAt);
            ps.setTimestamp(4, expiresAt);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public PasswordResetToken findValidTokenForgotPassword(String token) {
        String sql = "SELECT * FROM PasswordResetTokens WHERE token = ? AND expires_at > GETUTCDATE()";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PasswordResetToken prt = new PasswordResetToken();
                prt.setId(rs.getInt("id"));
                prt.setUserId(rs.getInt("UserID"));
                prt.setToken(token);
                return prt;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteTokenForgotPassword(int userId) {
        String sql = "DELETE FROM PasswordResetTokens WHERE UserID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int updatePasswordAfterForgot(int userId, String newPassword) {
        String hashPass = hashMD5(newPassword);
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashPass);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int saveToken(int userID, String token, Timestamp expiryDate) {
        String sql = "INSERT INTO RememberTokens (user_id, token, expiry_date) VALUES (?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.setString(2, token);
            ps.setTimestamp(3, expiryDate);
            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public User findByToken(String token) {
        String sql = "SELECT u.* FROM users u JOIN RememberTokens t ON u.UserID = t.user_id WHERE t.token = ? AND t.expiry_date > CURRENT_TIMESTAMP;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int UserID = rs.getInt("UserID");
                String UserName = rs.getString("UserName");
                String DisplayName = rs.getString("DisplayName");
                String Email = rs.getString("Email");
                String Password = rs.getString("Password");
                int roleInt = rs.getInt("Role");
                Role role = null;
                switch (roleInt) {
                    case 0:
                        role = Role.LEARNER;
                        break;
                    case 1:
                        role = Role.INSTRUCTOR;
                        break;
                    case 2:
                        role = Role.ADMIN;
                        break;
                    default:
                        System.err.println("Invalid role value from DB: " + roleInt);
                }
                int gender = rs.getInt("Gender");
                Timestamp BirthOfDay = rs.getTimestamp("DateOfBirth");
                Timestamp TimeCreate = rs.getTimestamp("UserCreateDate");
                byte[] Avatar = rs.getBytes("Avatar");
                String info = rs.getString("info");
                int banInt = rs.getInt("BanStatus");
                Ban Ban = null;
                switch (banInt) {
                    case 0:
                        Ban = Ban.NORMAL;
                        break;
                    case 1:
                        Ban = Ban.BANNED;
                        break;
                    default:
                        System.err.println("Invalid ban value from DB: " + banInt);
                }
                int ReportAmount = rs.getInt("ReportAmount");
                String PhoneNumber = rs.getString("PhoneNumber");
                boolean isVerified = rs.getBoolean("IsVerified");
                String GoogleID = rs.getString("GoogleID");

                User acc = new User(UserID, UserName, DisplayName, Email, Password, role, gender, TimeCreate, TimeCreate, Avatar, info, Ban, ReportAmount, info, isVerified, GoogleID);
                return acc;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int deleteToken(String token) {
        String sql = "DELETE FROM RememberTokens WHERE token = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int deleteAllTokens(int userID) {
        String sql = "DELETE FROM RememberTokens WHERE user_id = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            int result = ps.executeUpdate();
            return result > 0 ? 1 : 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static void main(String[] args) {
//        try {
//            UserDAO dao = new UserDAO();

//            String googleID = "123123";
//            String email = "admin01@example.com";
//            User acc = dao.findByGoogleID(googleID);
//            User user = dao.findByEmail(email);
//
//            System.out.println(acc);
//            System.out.println(user);
//            String username = "heroic";
//            String password = "123456";
//            String email = "admin01@example.com";
//
//            User acc = dao.verifyMD5(email, password);
//            System.out.println(acc);
//            Timestamp expiryDate = Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS));
//            int result = dao.saveToken(1, "1234567", expiryDate);
//
//        } catch (SQLException ex) {
//            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        String email = "student02@example.com";
//        User acc = dao.findByEmail(email);
//        if (acc != null) {
//            acc.setGoogleID("222333444555");
//            int result = dao.updateGoogleID(acc);
//            System.out.println(result);
//        }
//        UserDAO dao = new UserDAO();
//        User user = dao.getByUserID(2);
//        System.out.println(user);
//        UserDAO dao = new UserDAO();
//        PasswordResetToken pass = dao.findValidTokenForgotPassword("60b9f904-4887-41ab-bf7d-8cf2f3e8f499");
//        System.out.println(pass);
//        String googleID = "123123123";
//        String  email = "abc@gmail.com";
//
//        UserDAO userDao = new UserDAO();
//        UserGoogle user = new UserGoogle();
//        user.setName("heroic");
//        user.setEmail(email);
//        user.setPicture("https://www.facebook.com/");
//        user.setId(googleID);
//
//        int insert = userDao.insertGoogle(user);
//        System.out.println(insert);
//        UserDAO userDao = new UserDAO();
//        String token = "5593eaee-ea26-4323-8c94-303c70b758f6";
//        VerifyEmailToken verifyToken = userDao.findValidTokenVerifyEmail(token);
//        System.out.println(verifyToken);
    }
}
