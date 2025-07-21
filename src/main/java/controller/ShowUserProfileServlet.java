/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.UserDAO;
import dao.CourseDAO;
import dao.EnrollDAO;
import model.User;
import model.Role;
import model.Course;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import model.Enroll;

/**
 *
 * @author CE191059 Phuong Gia Lac
 */
@WebServlet(name="ShowUserProfileServlet", urlPatterns={"/viewprofile"})
public class ShowUserProfileServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private EnrollDAO enrollDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        courseDAO = new CourseDAO();
        enrollDAO = new EnrollDAO();
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ShowUserProfileServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ShowUserProfileServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        String userIdParam = request.getParameter("id");
        if (userIdParam == null || userIdParam.isEmpty()) {
            request.setAttribute("errorMessage", "User ID is required.");
            request.getRequestDispatcher("/WEB-INF/views/viewprofile.jsp").forward(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            User targetUser = userDAO.getByUserIDWithAvatar(userId);

            if (targetUser == null) {
                request.setAttribute("errorMessage", "User not found.");
                request.getRequestDispatcher("/WEB-INF/views/viewprofile.jsp").forward(request, response);
                return;
            }

            if (currentUser != null && currentUser.getUserId() == userId) {
                String redirectPath;
                switch (currentUser.getRole()) {
                    case ADMIN:
                        redirectPath = "/admin/profile";
                        break;
                    case INSTRUCTOR:
                        redirectPath = "/instructor/profile";
                        break;
                    case LEARNER:
                        redirectPath = "/learner/profile";
                        break;
                    default:
                        redirectPath = "/learner/profile";
                }
                response.sendRedirect(request.getContextPath() + redirectPath);
                return;
            }

            request.setAttribute("profile", targetUser);

            if (targetUser.getRole() == Role.INSTRUCTOR) {
                List<Course> courses = courseDAO.getCourseByUserID(userId);
                request.setAttribute("courses", courses);
            } else if (targetUser.getRole() == Role.LEARNER) {
                List<Enroll> enrollments = enrollDAO.getEnrolledCourse(userId);
                List<Course> completedCourses = new ArrayList<>();
                for (Enroll enrollment : enrollments) {
                    if (enrollment.getCompleteDate() != null && enrollment.getEnrollDate() != null) {
                        Course course = courseDAO.getCourseByCourseID(enrollment.getCourseID());
                        if (course != null) {
                            completedCourses.add(course);
                        }
                    }
                }
                request.setAttribute("completedCourses", completedCourses);
            }

            request.getRequestDispatcher("/WEB-INF/views/viewprofile.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid user ID format.");
            request.getRequestDispatcher("/WEB-INF/views/viewprofile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An error occurred while retrieving the profile.");
            request.getRequestDispatcher("/WEB-INF/views/viewprofile.jsp").forward(request, response);
        }
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
