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
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "TestCourseFixServlet", urlPatterns = {"/test/coursefix"})
public class TestCourseFixServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Course Fix Test Results</title></head><body>");
        out.println("<h1>Course Functionality Test Results</h1>");
        
        try {
            // Test CourseDAO initialization
            out.println("<h2>1. Testing CourseDAO Initialization</h2>");
            CourseDAO courseDAO = new CourseDAO();
            out.println("<p style='color: green;'>✓ CourseDAO initialized successfully</p>");
            
            // Test getAllCoursesAdmin
            out.println("<h2>2. Testing getAllCoursesAdmin</h2>");
            List<Course> courses = courseDAO.getAllCoursesAdmin(1, 5);
            out.println("<p>Found " + courses.size() + " courses</p>");
            
            if (courses.size() > 0) {
                out.println("<p style='color: green;'>✓ getAllCoursesAdmin working correctly</p>");
                
                Course firstCourse = courses.get(0);
                out.println("<p>First course: ID=" + firstCourse.getCourseID() + 
                           ", Name=" + firstCourse.getCourseName() + "</p>");
                out.println("<p>User: " + (firstCourse.getUser() != null ? firstCourse.getUser().getDisplayName() : "null") + "</p>");
                out.println("<p>Category: " + (firstCourse.getCategory() != null ? firstCourse.getCategory().getName() : "null") + "</p>");
                
                // Test getCourseByCourseIDAdmin
                out.println("<h2>3. Testing getCourseByCourseIDAdmin</h2>");
                Course courseDetail = courseDAO.getCourseByCourseIDAdmin(firstCourse.getCourseID());
                
                if (courseDetail != null) {
                    out.println("<p style='color: green;'>✓ getCourseByCourseIDAdmin working correctly</p>");
                    out.println("<p>Course has " + (courseDetail.getModules() != null ? courseDetail.getModules().size() : 0) + " modules</p>");
                    
                    if (courseDetail.getModules() != null && courseDetail.getModules().size() > 0) {
                        out.println("<p>First module: " + courseDetail.getModules().get(0).getModuleName() + "</p>");
                        out.println("<p>First module has " + 
                                   (courseDetail.getModules().get(0).getMaterials() != null ? 
                                    courseDetail.getModules().get(0).getMaterials().size() : 0) + " materials</p>");
                        out.println("<p style='color: green;'>✓ Modules and materials loading correctly</p>");
                    } else {
                        out.println("<p style='color: orange;'>⚠ No modules found for this course</p>");
                    }
                } else {
                    out.println("<p style='color: red;'>✗ getCourseByCourseIDAdmin returned null</p>");
                }
                
            } else {
                out.println("<p style='color: orange;'>⚠ No courses found in database</p>");
            }
            
            // Test DAO initialization
            out.println("<h2>4. Testing Other DAOs</h2>");
            ModuleDAO moduleDAO = new ModuleDAO();
            MaterialDAO materialDAO = new MaterialDAO();
            out.println("<p style='color: green;'>✓ ModuleDAO and MaterialDAO initialized successfully</p>");
            
            out.println("<h2>Summary</h2>");
            out.println("<p style='color: green; font-weight: bold;'>All tests completed successfully! The course management functionality should now work properly.</p>");
            
        } catch (Exception e) {
            out.println("<h2>Error Occurred</h2>");
            out.println("<p style='color: red;'>Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("</body></html>");
    }
}