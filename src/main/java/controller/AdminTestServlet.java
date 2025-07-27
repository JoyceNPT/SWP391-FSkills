package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;
import model.Module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for admin test management
 * Allows admins to view and delete tests created by instructors
 */
@WebServlet(name = "AdminTestServlet", urlPatterns = {"/admin/tests"})
public class AdminTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();

        TestDAO testDAO = new TestDAO();
        QuestionDAO questionDAO = new QuestionDAO();
        ModuleDAO moduleDAO = new ModuleDAO();
        CourseDAO courseDAO = new CourseDAO();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        if (user.getRole() != Role.ADMIN) {
            response.sendRedirect(contextPath + "/homePage_Guest.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    List<Course> courses = courseDAO.getAllCoursesAdmin(1, 1000);
                    request.setAttribute("courses", courses);

                    String filterCourseIdStr = request.getParameter("filterCourseId");
                    String filterModuleIdStr = request.getParameter("filterModuleId");

                    List<Test> testList = new ArrayList<>();

                    if (filterCourseIdStr != null && !filterCourseIdStr.isEmpty()) {
                        int courseId = Integer.parseInt(filterCourseIdStr);
                        List<Module> modulesByCourse = moduleDAO.getAllModuleByCourseID(courseId);
                        request.setAttribute("filterModules", modulesByCourse);

                        if (filterModuleIdStr != null && !filterModuleIdStr.isEmpty()) {
                            int moduleId = Integer.parseInt(filterModuleIdStr);
                            testList = testDAO.getTestsByModuleID(moduleId);
                        } else {
                            for (Module module : modulesByCourse) {
                                testList.addAll(testDAO.getTestsByModuleID(module.getModuleID()));
                            }
                        }
                    } else {
                        testList = testDAO.getAllTestsWithModuleCourse();
                    }

                    for (Test test : testList) {
                        int questionCount = questionDAO.getQuestionCountForTest(test.getTestID());
                        test.setQuestionCount(questionCount);
                    }

                    request.setAttribute("listTest", testList);
                    request.getRequestDispatcher("/WEB-INF/views/adminListTest.jsp").forward(request, response);
                    break;

                case "view":
                    String testIdView = request.getParameter("testId");
                    if (testIdView == null || testIdView.isEmpty()) {
                        response.sendRedirect(contextPath + "/admin/tests?action=list");
                        return;
                    }

                    int viewTestId = Integer.parseInt(testIdView);
                    Test testToView = testDAO.getTestByID(viewTestId);

                    if (testToView == null) {
                        session.setAttribute("err", "Test not found.");
                        response.sendRedirect(contextPath + "/admin/tests?action=list");
                        return;
                    }

                    List<Question> viewQuestions = questionDAO.getQuestionsByTestID(viewTestId);
                    int totalPoints = questionDAO.getTotalPointsForTest(viewTestId);
                    int questionCount = questionDAO.getQuestionCountForTest(viewTestId);

                    request.setAttribute("test", testToView);
                    request.setAttribute("questions", viewQuestions);
                    request.setAttribute("totalPoints", totalPoints);
                    request.setAttribute("questionCount", questionCount);
                    request.getRequestDispatcher("/WEB-INF/views/adminViewTest.jsp").forward(request, response);
                    break;

                case "getModules":
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    String courseIdStr = request.getParameter("courseId");
                    if (courseIdStr == null || courseIdStr.isEmpty()) {
                        response.getWriter().write("[]");
                        return;
                    }

                    try {
                        int courseId = Integer.parseInt(courseIdStr);
                        System.out.println("[DEBUG] courseId nhận được: " + courseId);

                        List<Module> modules = moduleDAO.getAllModuleByCourseID(courseId);
                        System.out.println("[DEBUG] Số module lấy được: " + modules.size());
                        for (Module m : modules) {
                            System.out.println("[DEBUG] ModuleID: " + m.getModuleID() + ", ModuleName: " + m.getModuleName());
                        }

                        StringBuilder json = new StringBuilder();
                        json.append("[");
                        for (int i = 0; i < modules.size(); i++) {
                            Module m = modules.get(i);
                            json.append("{");
                            json.append("\"moduleID\":" + m.getModuleID() + ",");
                            json.append("\"moduleName\":\"" + m.getModuleName().replace("\"", "\\\"") + "\"");
                            json.append("}");
                            if (i < modules.size() - 1) {
                                json.append(",");
                            }
                        }
                        json.append("]");

                        response.getWriter().write(json.toString());
                    } catch (NumberFormatException e) {
                        response.getWriter().write("[]");
                    }
                    break;

                default:
                    response.sendRedirect(contextPath + "/admin/tests?action=list");
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("err", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/adminListTest.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();

        TestDAO testDAO = new TestDAO();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        if (user.getRole() != Role.ADMIN) {
            response.sendRedirect(contextPath + "/homePage_Guest.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(contextPath + "/admin/tests?action=list");
            return;
        }

        try {
            if (action.equals("delete")) {
                handleDeleteTest(request, response, testDAO, session);
            } else {
                response.sendRedirect(contextPath + "/admin/tests?action=list");
            }
        } catch (Exception e) {
            session.setAttribute("err", "Error: " + e.getMessage());
            response.sendRedirect(contextPath + "/admin/tests?action=list");
        }
    }

    private void handleDeleteTest(HttpServletRequest request, HttpServletResponse response,
                                  TestDAO testDAO, HttpSession session)
            throws ServletException, IOException {

        String testIdStr = request.getParameter("testId");
        String contextPath = request.getContextPath();

        try {
            int testId = Integer.parseInt(testIdStr);

            Test test = testDAO.getTestByID(testId);
            if (test == null) {
                session.setAttribute("err", "Test not found.");
                response.sendRedirect(contextPath + "/admin/tests?action=list");
                return;
            }

            int deleteResult = testDAO.deleteTest(testId);

            if (deleteResult > 0) {
                session.setAttribute("success", "Test deleted successfully!");
            } else {
                session.setAttribute("err", "Failed to delete test.");
            }
            response.sendRedirect(contextPath + "/admin/tests?action=list");
        } catch (NumberFormatException e) {
            session.setAttribute("err", "Invalid test ID.");
            response.sendRedirect(contextPath + "/admin/tests?action=list");
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin Test Management Servlet";
    }
}
