package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import dao.CategoryDAO;
import dao.CourseDAO;
import dao.ModuleDAO;
import dao.EnrollDAO;
import dao.StudyDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.Category;
import model.Module;
import model.Course;
import model.Role;
import model.User;
import model.Enroll;

/**
 * @author Ngo Phuoc Thinh - CE170008 - SE1815
 */
@WebServlet(name = "InstructorModuleServlet", urlPatterns = {"/instructor/courses/modules"})
public class InstructorModuleServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet InstructorModuleServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InstructorModuleServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet requestF
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();

        ModuleDAO mDao = new ModuleDAO();
        CourseDAO cDao = new CourseDAO();
        CategoryDAO catDao = new CategoryDAO();
        EnrollDAO enrollDAO = new EnrollDAO();
        UserDAO userDAO = new UserDAO();
        StudyDAO studyDAO = new StudyDAO();

        Course course = null;

        User acc = (User) session.getAttribute("user");
        if (acc == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        if (acc.getRole() != Role.INSTRUCTOR) {
            response.sendRedirect(contextPath + "/homePage_Guest.jsp");
            return;
        }

        String courseIdParam = request.getParameter("courseId");
        int courseId = -1;

        try {
            courseId = Integer.parseInt(courseIdParam);
            course = cDao.getCourseByCourseID(courseId);
            session.setAttribute("course", course);

            if (course == null) {
                response.sendRedirect(contextPath + "/instructor/courses");
                return;
            }

            String action = (String) request.getParameter("action");
            if (action == null) {
                action = "list";
            }

            switch (action) {
                case "list":
                    List<Category> listCat = catDao.getAllCategory();
                    List<Module> list = mDao.getAllModuleByCourseID(courseId);
                    
                    List<Enroll> enrollments = enrollDAO.getEnrolledCourseByCourseID(courseId);
                    List<Map<String, Object>> enrolledUsers = new ArrayList<>();
                    int totalStudents = enrollments.size();
                    int completedStudents = 0;

                    for (Enroll enrollment : enrollments) {
                        if (enrollment.getCompleteDate() != null && enrollment.getEnrollDate() != null) {
                            completedStudents++;
                        }
                        User user = userDAO.getByUserID(enrollment.getUserID());
                        if (user != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("user", user);
                            userData.put("progress", studyDAO.returnStudyProgress(user.getUserId(), courseId));
                            enrolledUsers.add(userData);
                        }
                    }

                    request.setAttribute("listModule", list);
                    request.setAttribute("listCategory", listCat);
                    request.setAttribute("enrolledUsers", enrolledUsers);
                    request.setAttribute("totalStudents", totalStudents);
                    request.setAttribute("completedStudents", completedStudents);
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                default:
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CourseDAO cDao = new CourseDAO();
        ModuleDAO mDao = new ModuleDAO();

        Course course = null;
        Module module = null;

        int courseID = Integer.parseInt(request.getParameter("courseID"));

        if (request.getMethod().equalsIgnoreCase("POST")) {
            String action = request.getParameter("action");

            if (action.equalsIgnoreCase("create")) {
                course = cDao.getCourseByCourseID(courseID);

                String moduleName = request.getParameter("moduleName");

                if (moduleName == null) {
                    moduleName = "";
                }
                moduleName = moduleName.trim();

                if (moduleName.isEmpty()) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("listModule", list);
                    request.setAttribute("err", "Create failed: Module Name is required.");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                    return;
                }

                if (moduleName.contains("  ")) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("listModule", list);
                    request.setAttribute("err", "Create failed: Module Name must not contain consecutive spaces.");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                    return;
                }

                module = new Module(moduleName, course);

                int insert = mDao.insertModule(module);

                if (insert > 0) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("success", "Module created successfully!!!");
                    request.setAttribute("listModule", list);
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                } else {
                    request.setAttribute("err", "Create failed: Unknown error!");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                }
            } else if (action.equalsIgnoreCase("update")) {
                int moduleID = Integer.parseInt(request.getParameter("moduleID"));
                String moduleName = request.getParameter("moduleName");

                if (moduleName == null) {
                    moduleName = "";
                }
                moduleName = moduleName.trim();

                if (moduleName.isEmpty()) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("listModule", list);
                    request.setAttribute("err", "Create failed: Module Name is required.");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                    return;
                }

                if (moduleName.contains("  ")) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("listModule", list);
                    request.setAttribute("err", "Create failed: Module Name must not contain consecutive spaces.");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                    return;
                }

                int update = mDao.updateModule(moduleID, moduleName);

                if (update > 0) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("success", "Module updated successfully!!!");
                    request.setAttribute("listModule", list);
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                } else {
                    request.setAttribute("err", "Update failed: Unknown error!");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                }
            } else if (action.equalsIgnoreCase("delete")) {
                int moduleID = Integer.parseInt(request.getParameter("moduleID"));

                int delete = mDao.updateStatusModule(moduleID);

                if (delete > 0) {
                    List<Module> list = mDao.getAllModuleByCourseID(courseID);

                    request.setAttribute("success", "Course deleted successfully!!!");
                    request.setAttribute("listModule", list);
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                } else {
                    request.setAttribute("err", "Delete failed: Unknown error!");
                    request.getRequestDispatcher("/WEB-INF/views/listModule.jsp").forward(request, response);
                }
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
