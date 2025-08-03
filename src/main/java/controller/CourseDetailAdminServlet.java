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

    // DAO instances to avoid code duplication
    private CourseDAO courseDAO;
    private ModuleDAO moduleDAO;
    private MaterialDAO materialDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        moduleDAO = new ModuleDAO();
        materialDAO = new MaterialDAO();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Fetches and displays a specific course's details for an admin.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");

        // Check for user login and role (must be ADMIN)
        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String courseIdParam = request.getParameter("courseID");
        int courseId = -1;

        if (courseIdParam == null || courseIdParam.isEmpty()) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Course ID is missing.");
            return;
        }

        try {
            courseId = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Invalid Course ID format.");
            return;
        }

        // Fetch course details
        Course course = courseDAO.getCourseByCourseID(courseId);

        if (course == null) {
            response.sendRedirect(contextPath + "/admin/ManageCourse?error=Course does not exist.");
            return;
        }

        // Fetch modules and their materials for the course - với xử lý lỗi tốt hơn
        List<Module> modules = new ArrayList<>();
        try {
            modules = moduleDAO.getAllModuleByCourseID(courseId);

            // Đảm bảo modules không null
            if (modules == null) {
                modules = new ArrayList<>();
                System.out.println("Warning: moduleDAO.getAllModuleByCourseID returned null for courseID: " + courseId);
            }

            // Fetch materials cho từng module
            if (!modules.isEmpty()) {
                for (Module module : modules) {
                    List<Material> materials = new ArrayList<>();
                    try {
                        materials = materialDAO.getMaterialsByModuleIDAdmin(module.getModuleID());

                        // Đảm bảo materials không null
                        if (materials == null) {
                            materials = new ArrayList<>();
                            System.out.println("Warning: materialDAO.getMaterialsByModuleIDAdmin returned null for moduleID: " + module.getModuleID());
                        }

                        module.setMaterials(materials);
                        System.out.println("Loaded " + materials.size() + " materials for module " + module.getModuleID() + ": " + module.getModuleName());

                    } catch (SQLException e) {
                        System.err.println("Error fetching materials for module " + module.getModuleID() + ": " + e.getMessage());
                        e.printStackTrace();
                        module.setMaterials(new ArrayList<>());
                    } catch (Exception e) {
                        System.err.println("Unexpected error fetching materials for module " + module.getModuleID() + ": " + e.getMessage());
                        e.printStackTrace();
                        module.setMaterials(new ArrayList<>());
                    }
                }
            }

            System.out.println("Successfully loaded " + modules.size() + " modules for course " + courseId);

        } catch (Exception e) {
            System.err.println("Error fetching modules for course " + courseId + ": " + e.getMessage());
            e.printStackTrace();
            modules = new ArrayList<>();
        }

        // Set modules vào course (đã được đảm bảo không null)
        course.setModules(modules);

        // Debug logging
        System.out.println("Course ID: " + course.getCourseID() + ", Course Name: " + course.getCourseName());
        System.out.println("Total modules: " + (course.getModules() != null ? course.getModules().size() : 0));

        if (course.getModules() != null) {
            for (Module module : course.getModules()) {
                System.out.println("Module: " + module.getModuleName() +
                        ", Materials count: " + (module.getMaterials() != null ? module.getMaterials().size() : 0));
            }
        }

        // Set attributes and forward to JSP
        request.setAttribute("course", course);

        // Forward to the admin's course details JSP
        request.getRequestDispatcher("/WEB-INF/views/courseDetailAdmin.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Manages admin actions such as updating approval status and deleting modules/materials.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

        int courseID = Integer.parseInt(courseIdParam);
        String redirectURL = contextPath + "/admin/CourseDetailAdmin?courseID=" + courseID;
        String successMessage = null;
        String errorMessage = null;

        if (action == null) {
            response.sendRedirect(redirectURL + "&error=" + java.net.URLEncoder.encode("Invalid action.", "UTF-8"));
            return;
        }

        try {
            switch (action) {
                case "updateStatus":
                    String statusParam = request.getParameter("status");
                    if (statusParam == null || statusParam.isEmpty()) {
                        errorMessage = "Update status failed: Status is missing.";
                        break;
                    }
                    int status = Integer.parseInt(statusParam);
                    if (courseDAO.updateCourseStatus(courseID, status)) {
                        successMessage = "Course status updated successfully!";
                    } else {
                        errorMessage = "Failed to update course status.";
                    }
                    break;

                case "deleteModule":
                    String moduleIdParam = request.getParameter("moduleID");
                    if (moduleIdParam == null || moduleIdParam.isEmpty()) {
                        errorMessage = "Delete module failed: Module ID is missing.";
                        break;
                    }
                    int moduleId = Integer.parseInt(moduleIdParam);
                    if (moduleDAO.updateStatusModule(moduleId) > 0) {
                        successMessage = "Module deleted successfully!";
                    } else {
                        errorMessage = "Failed to delete module.";
                    }
                    break;

                case "deleteMaterialAdmin":
                    String materialIdParam = request.getParameter("materialID");
                    if (materialIdParam == null || materialIdParam.isEmpty()) {
                        errorMessage = "Delete material failed: Material ID is missing.";
                        break;
                    }
                    int materialId = Integer.parseInt(materialIdParam);
                    try {
                        if (materialDAO.deleteMaterialAdmin(materialId) > 0) {
                            successMessage = "Material deleted successfully!";
                        } else {
                            errorMessage = "Failed to delete material.";
                        }
                    } catch (SQLException e) {
                        errorMessage = "Database error while deleting material: " + e.getMessage();
                    }
                    break;

                default:
                    errorMessage = "Invalid action.";
                    break;
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid input format.";
        } catch (Exception e) {
            errorMessage = "Unexpected error: " + e.getMessage();
            System.err.println("Unexpected error in doPost: " + e.getMessage());
            e.printStackTrace();
        }

        // Redirect with message
        if (successMessage != null) {
            response.sendRedirect(redirectURL + "&message=" + java.net.URLEncoder.encode(successMessage, "UTF-8"));
        } else if (errorMessage != null) {
            response.sendRedirect(redirectURL + "&error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        } else {
            response.sendRedirect(redirectURL);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for admin to view and manage course details";
    }
}
