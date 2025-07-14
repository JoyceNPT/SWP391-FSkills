package controller;

import dao.CourseDAO;
import dao.ModuleDAO;
import dao.MaterialDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ManageCourseServlet", urlPatterns = {"/admin/ManageCourse"})
public class ManageCourseServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private ModuleDAO moduleDAO;
    private MaterialDAO materialDAO;

    @Override
    public void init() throws ServletException {
        try {
            courseDAO = new CourseDAO();
            moduleDAO = new ModuleDAO();
            materialDAO = new MaterialDAO();
        } catch (Exception e) {
            throw new ServletException("Error initializing DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 10;

        if (action != null && action.equals("detail")) {
            try {
                int courseID = Integer.parseInt(request.getParameter("id"));
                Course course = courseDAO.getCourseByCourseIDAdmin(courseID);
                if (course != null) {
                    request.setAttribute("course", course);
                    request.getRequestDispatcher("/WEB-INF/views/courseDetailAdmin.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Course not found.");
                    loadCoursesAndForward(request, response, page, pageSize);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid course ID.");
                loadCoursesAndForward(request, response, page, pageSize);
            } catch (java.sql.SQLException e) {
                request.setAttribute("error", "Database error occurred.");
                loadCoursesAndForward(request, response, page, pageSize);
            }
        } else {
            loadCoursesAndForward(request, response, page, pageSize);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 10;
        int courseID = request.getParameter("courseID") != null ? Integer.parseInt(request.getParameter("courseID")) : 0;

        if (action != null) {
            try {
                if (action.equals("delete")) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    int result = courseDAO.deleteCourseAdmin(id);
                    if (result > 0) {
                        request.setAttribute("message", "Course deleted successfully.");
                    } else if (result == -1) {
                        request.setAttribute("error", "Cannot delete course. There are students currently enrolled in this course.");
                    } else {
                        request.setAttribute("error", "Course not found or could not be deleted.");
                    }
                    loadCoursesAndForward(request, response, page, pageSize);
                } else if (action.equals("deleteModule")) {
                    int moduleID = Integer.parseInt(request.getParameter("moduleID"));
                    int result = moduleDAO.deleteModule(moduleID);
                    if (result > 0) {
                        request.setAttribute("message", "Module deleted successfully.");
                    } else {
                        request.setAttribute("error", "Module not found or could not be deleted.");
                    }
                    redirectToCourseDetail(request, response, courseID, page, pageSize);
                } else if (action.equals("deleteMaterialAdmin")) {
                    int materialID = Integer.parseInt(request.getParameter("materialID"));
                    int result = materialDAO.deleteMaterialAdmin(materialID);
                    if (result > 0) {
                        request.setAttribute("message", "Material deleted successfully.");
                    } else {
                        request.setAttribute("error", "Material not found or could not be deleted.");
                    }
                    redirectToCourseDetail(request, response, courseID, page, pageSize);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid ID.");
                loadCoursesAndForward(request, response, page, pageSize);
            } catch (java.sql.SQLException e) {
                request.setAttribute("error", "Database error occurred.");
                loadCoursesAndForward(request, response, page, pageSize);
            }
        } else {
            loadCoursesAndForward(request, response, page, pageSize);
        }
    }

    private void loadCoursesAndForward(HttpServletRequest request, HttpServletResponse response, int page, int pageSize) throws ServletException, IOException {
        List<Course> courses = courseDAO.getAllCoursesAdmin(page, pageSize);
        int totalCourses = courseDAO.getTotalCoursesCount();
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/courseManage.jsp").forward(request, response);
    }

    private void redirectToCourseDetail(HttpServletRequest request, HttpServletResponse response, int courseID, int page, int pageSize) throws ServletException, IOException {
        try {
            Course course = courseDAO.getCourseByCourseIDAdmin(courseID);
            if (course != null) {
                request.setAttribute("course", course);
                request.getRequestDispatcher("/WEB-INF/views/courseDetailAdmin.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Course not found.");
                loadCoursesAndForward(request, response, page, pageSize);
            }
        } catch (java.sql.SQLException e) {
            request.setAttribute("error", "Database error occurred.");
            loadCoursesAndForward(request, response, page, pageSize);
        }
    }
}
