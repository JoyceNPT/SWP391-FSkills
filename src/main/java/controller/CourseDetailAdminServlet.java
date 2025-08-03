package controller;

import java.io.IOException;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;



import dao.CourseDAO;
import dao.ModuleDAO;

import dao.MaterialDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Course;
import model.Role;
import model.User;
import model.Module;
import model.Material;

/**
 * Servlet to handle course details and administrative actions for an admin.
 * URL pattern: /admin/CourseDetailAdmin
 *
 * @author NguyenThanhHuy - CE182349 - SE1815
 */
@WebServlet(name = "CourseDetailAdminServlet", urlPatterns = {"/admin/CourseDetailAdmin"})
public class CourseDetailAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CourseDAO courseDAO;
    private ModuleDAO moduleDAO;
    private MaterialDAO materialDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
        moduleDAO = new ModuleDAO();
        materialDAO = new MaterialDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String courseIdParam = request.getParameter("courseID");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Course ID is missing.");
            return;
        }

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Invalid Course ID format.");
            return;
        }

        Course course = courseDAO.getCourseByCourseID(courseId);
        if (course == null) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Course does not exist.");
            return;
        }

        try {
            List<Module> modules = moduleDAO.getAllModuleByCourseID(courseId);
            if (modules == null) {
                modules = new ArrayList<>();
            }

            for (Module module : modules) {
                try {
                    List<Material> materials = materialDAO.getMaterialsByModuleIDAdmin(module.getModuleID());
                    if (materials == null) {
                        materials = new ArrayList<>();
                    }
                    module.setMaterials(materials);
                } catch (SQLException e) {
                    module.setMaterials(new ArrayList<>());
                    System.err.println("Error fetching materials for module " + module.getModuleID() + ": " + e.getMessage());
                }
            }
            course.setModules(modules);

        } catch (Exception e) {
            course.setModules(new ArrayList<>());
            System.err.println("Error fetching modules for course " + courseId + ": " + e.getMessage());
        }

        request.setAttribute("course", course);
        request.getRequestDispatcher("/WEB-INF/views/courseDetailAdmin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String action = request.getParameter("action");
        String courseIdParam = request.getParameter("courseID");

        if (courseIdParam == null || courseIdParam.isEmpty()) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Invalid request: Course ID is missing.");
            return;
        }

        int courseID;
        try {
            courseID = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Invalid Course ID format.");
            return;
        }

        String message = null;
        String error = null;

        try {
            switch (action) {
                case "updateStatus":
                    String statusParam = request.getParameter("status");
                    if (statusParam == null || statusParam.isEmpty()) {
                        error = "Status is missing.";
                        break;
                    }
                    int status = Integer.parseInt(statusParam);
                    if (courseDAO.updateCourseStatus(courseID, status)) {
                        message = "Course status updated successfully!";
                    } else {
                        error = "Failed to update course status.";
                    }
                    break;

                case "deleteModule":
                    String moduleIdParam = request.getParameter("moduleID");
                    if (moduleIdParam == null || moduleIdParam.isEmpty()) {
                        error = "Module ID is missing.";
                        break;
                    }
                    int moduleId = Integer.parseInt(moduleIdParam);
                    if (moduleDAO.updateStatusModule(moduleId) > 0) {
                        message = "Module deleted successfully!";
                    } else {
                        error = "Failed to delete module.";
                    }
                    break;

                case "deleteMaterialAdmin":
                    String materialIdParam = request.getParameter("materialID");
                    if (materialIdParam == null || materialIdParam.isEmpty()) {
                        error = "Material ID is missing.";
                        break;
                    }
                    int materialId = Integer.parseInt(materialIdParam);
                    if (materialDAO.deleteMaterialAdmin(materialId) > 0) {
                        message = "Material deleted successfully!";
                    } else {
                        error = "Failed to delete material.";
                    }
                    break;

                default:
                    error = "Invalid action.";
                    break;
            }
        } catch (Exception e) {
            error = "Unexpected error: " + e.getMessage();
            e.printStackTrace();
        }

        // Redirect lại trang detail để load dữ liệu mới từ DB
        String redirectURL = contextPath + "/admin/CourseDetailAdmin?courseID=" + courseID;
        if (message != null) {
            response.sendRedirect(redirectURL + "&message=" + java.net.URLEncoder.encode(message, "UTF-8"));
        } else if (error != null) {
            response.sendRedirect(redirectURL + "&error=" + java.net.URLEncoder.encode(error, "UTF-8"));
        } else {
            response.sendRedirect(redirectURL);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for admin to view and manage course details";
    }
}
