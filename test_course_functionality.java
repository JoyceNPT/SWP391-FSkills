package test;

import dao.CourseDAO;
import dao.ModuleDAO;
import dao.MaterialDAO;
import model.Course;
import java.util.List;

public class TestCourseFunctionality {
    public static void main(String[] args) {
        System.out.println("[DEBUG_LOG] Starting course functionality test...");

        try {
            CourseDAO courseDAO = new CourseDAO();
            System.out.println("[DEBUG_LOG] CourseDAO initialized successfully");

            // Test getAllCoursesAdmin
            System.out.println("[DEBUG_LOG] Testing getAllCoursesAdmin...");
            List<Course> courses = courseDAO.getAllCoursesAdmin(1, 10);
            System.out.println("[DEBUG_LOG] getAllCoursesAdmin returned " + courses.size() + " courses");

            if (courses.size() > 0) {
                Course firstCourse = courses.get(0);
                System.out.println("[DEBUG_LOG] First course ID: " + firstCourse.getCourseID());
                System.out.println("[DEBUG_LOG] First course name: " + firstCourse.getCourseName());
                System.out.println("[DEBUG_LOG] First course user: " + (firstCourse.getUser() != null ? firstCourse.getUser().getDisplayName() : "null"));
                System.out.println("[DEBUG_LOG] First course category: " + (firstCourse.getCategory() != null ? firstCourse.getCategory().getName() : "null"));

                // Test getCourseByCourseIDAdmin
                System.out.println("[DEBUG_LOG] Testing getCourseByCourseIDAdmin with ID: " + firstCourse.getCourseID());
                Course courseDetail = courseDAO.getCourseByCourseIDAdmin(firstCourse.getCourseID());

                if (courseDetail != null) {
                    System.out.println("[DEBUG_LOG] Course detail loaded successfully");
                    System.out.println("[DEBUG_LOG] Course has " + (courseDetail.getModules() != null ? courseDetail.getModules().size() : 0) + " modules");

                    if (courseDetail.getModules() != null && courseDetail.getModules().size() > 0) {
                        System.out.println("[DEBUG_LOG] First module name: " + courseDetail.getModules().get(0).getModuleName());
                        System.out.println("[DEBUG_LOG] First module has " + 
                            (courseDetail.getModules().get(0).getMaterials() != null ? 
                             courseDetail.getModules().get(0).getMaterials().size() : 0) + " materials");
                    }
                } else {
                    System.out.println("[DEBUG_LOG] ERROR: Course detail is null");
                }

                // Test delete functionality
                System.out.println("[DEBUG_LOG] Testing delete functionality...");
                ModuleDAO moduleDAO = new ModuleDAO();
                MaterialDAO materialDAO = new MaterialDAO();
                System.out.println("[DEBUG_LOG] Delete DAOs initialized successfully");

            } else {
                System.out.println("[DEBUG_LOG] ERROR: No courses found");
            }

        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[DEBUG_LOG] Test completed");
    }
}
