package controller;

import dao.CourseDAO;
import dao.MaterialDAO;
import dao.ModuleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Module;
import model.Role;
import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.Material;
import util.ImageBase64;

/**
 * @author Hua Khanh Duy - CE180230 - SE1815
 */
@MultipartConfig
@WebServlet(name = "InstructorMaterial", urlPatterns = {"/instructor/courses/modules/material"})
public class InstructorMaterialServlet extends HttpServlet {

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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        MaterialDAO madao = new MaterialDAO();
        ModuleDAO mdao = new ModuleDAO();
        CourseDAO cdao = new CourseDAO();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        if (user.getRole() != Role.INSTRUCTOR) {
            response.sendRedirect("homePage_Guest.jsp");
            return;
        }
        String course = request.getParameter("courseId");
        String module = request.getParameter("moduleId");
        String material = request.getParameter("materialId");
        int courseId = -1;
        int moduleId = -1;
        int materialId = -1;
        try {
            String action = (String) request.getParameter("action");
            if (action == null) {
                action = "listMaterial";
            }
            if (action.equalsIgnoreCase("listMaterial")) {
                courseId = Integer.parseInt(course);
                moduleId = Integer.parseInt(module);
                Module m = mdao.getModuleByID(moduleId);
                List<Material> listMaterial = madao.getAllMaterial(courseId, moduleId);

                for (Material ma : listMaterial) {
                    if ("pdf".equalsIgnoreCase(ma.getType()) && ma.getMaterialFile() != null) {
                        String uri = ImageBase64.toDataURI(ma.getMaterialFile(), "application/pdf");
                        ma.setPdfDataURI(uri); // cần có setter trong class Material
                    }
                }
                request.setAttribute("module", m);
                request.setAttribute("listMaterial", listMaterial);
                request.getRequestDispatcher("/WEB-INF/views/listMaterials.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("create")) {

                moduleId = Integer.parseInt(module);
                Module mo = mdao.getModuleByID(moduleId);
                String YOUTUBE_API_KEY = System.getenv("YOUTUBE_API_KEY");
                
                request.setAttribute("module", mo);
                session.setAttribute("apiKey", YOUTUBE_API_KEY);
                request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("update")) {
                moduleId = Integer.parseInt(module);
                materialId = Integer.parseInt(material);
                Material ma = madao.getMaterialById(materialId);
                Module mo = mdao.getModuleByID(moduleId);
                String YOUTUBE_API_KEY = System.getenv("YOUTUBE_API_KEY");
                
                session.setAttribute("apiKey", YOUTUBE_API_KEY);
                request.setAttribute("material", ma);
                request.setAttribute("module", mo);
                request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
            } else if (action.equalsIgnoreCase("details")) {
                moduleId = Integer.parseInt(module);
                materialId = Integer.parseInt(material);
                Material ma = madao.getMaterialById(materialId);
                Module mo = mdao.getModuleByID(moduleId);
                request.setAttribute("material", ma);
                request.setAttribute("module", mo);
                request.getRequestDispatcher("/WEB-INF/views/materialDetails.jsp").forward(request, response);
            }
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.print(e.getMessage());
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
        String course = request.getParameter("courseId");
        String module = request.getParameter("moduleId");
        String material = request.getParameter("materialId");
        CourseDAO cdao = new CourseDAO();
        ModuleDAO mdao = new ModuleDAO();
        MaterialDAO madao = new MaterialDAO();
        int courseId = -1;
        int moduleId = -1;
        int materialId = -1;
        if (request.getMethod().equalsIgnoreCase("POST")) {
            String action = request.getParameter("action");

            if (action.equalsIgnoreCase("create")) {
                String courseIdStr = request.getParameter("courseId");
                String moduleIdStr = request.getParameter("moduleId");
                String materialName = request.getParameter("materialName");

                if (materialName == null || materialName.trim().isEmpty() || materialName.matches(".*\\s{2,}.*")) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material name must not contain consecutive spaces!");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }

                if (materialName.length() > 255) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material name cannot exceed 255 letters!");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }
                String type = request.getParameter("type");
                if (!type.equalsIgnoreCase("video") && !type.equalsIgnoreCase("pdf") && !type.equalsIgnoreCase("link")) {
                    moduleId = Integer.parseInt(module);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "The material type must be either video, PDF/Doc, or link — no other types are allowed.");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }

                String materialOrderStr = request.getParameter("materialOrder");
                if (materialOrderStr == null || materialOrderStr.trim().isEmpty()) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material order must not be empty.");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }

                int materialOrder;
                try {
                    materialOrder = Integer.parseInt(materialOrderStr.trim());
                    courseId = Integer.parseInt(courseIdStr);
                    moduleId = Integer.parseInt(module);

                    if (materialOrder < 0 || materialOrder > 10) {
                        moduleId = Integer.parseInt(module);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "Material order must be a non-negative number.");
                        request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                        return;
                    }

                    boolean isExisted = madao.checkMaterialOrderExists(materialOrder, moduleId, courseId);

                    if (isExisted) {
                        moduleId = Integer.parseInt(module);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "This Material Order already exists. Please choose a different number.");
                        request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                        return;
                    }

                } catch (NumberFormatException e) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material order must be a valid integer.");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }

                String videoTimeStr = request.getParameter("videoTime");

                if (videoTimeStr == null || videoTimeStr.trim().isEmpty()) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Video time cannot empty!");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }

                // Định dạng: hh:mm:ss hoặc mm:ss
                String timePattern = "^((\\d{1,2}):)?([0-5]?\\d):([0-5]\\d)$";
                if (!videoTimeStr.matches(timePattern)) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Video time must be in format hh:mm:ss or mm:ss (e.g., 01:30 or 00:05:30).");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }

                String materialDescription = request.getParameter("materialDescription");
                if (materialDescription == null || materialDescription.trim().isEmpty() || materialDescription.matches(".*\\s{2,}.*")) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material description must not be empty.");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);
                    return;
                }
                if (materialDescription != null) {
                    // Xóa khoảng trắng ở đầu mỗi dòng
                    materialDescription = Arrays.stream(materialDescription.split("\n"))
                            .map(String::stripLeading)
                            .collect(Collectors.joining("\n"));
                }
                String materialUrl = "";

                if ("video".equalsIgnoreCase(type)) {
                    materialUrl = request.getParameter("materialVideo");
                    // Kiểm tra và chuẩn hóa nếu cần
                    if (materialUrl == null || materialUrl.trim().isEmpty()) {
                        materialUrl = "";
                    }

                } else if ("link".equalsIgnoreCase(type)) {
                    materialUrl = request.getParameter("materialLink");
                    if (materialUrl == null || materialUrl.trim().isEmpty()) {
                        materialUrl = ""; // hoặc thông báo lỗi
                    }
                } else {
                    materialUrl = "";
                }
                // Nhận file
                Part filePart = request.getPart("docFile");

                InputStream materialFile = null;
                String fileName = null;

                if (filePart != null && filePart.getSize() > 0) {
                    // Lấy nội dung file thành mảng byte
                    materialFile = filePart.getInputStream();

                    // Lấy tên gốc của file
                    String contentDisp = filePart.getHeader("content-disposition");
                    for (String token : contentDisp.split(";")) {
                        if (token.trim().startsWith("filename")) {
                            fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                            break;
                        }
                    }
                }
                try {
                    moduleId = Integer.parseInt(moduleIdStr);
                    courseId = Integer.parseInt(courseIdStr);

                    MaterialDAO dao = new MaterialDAO();
                    ModuleDAO moddao = new ModuleDAO();
                    CourseDAO coudao = new CourseDAO();
                    int res = dao.insertMaterial(moduleId, materialName, type, materialOrder,
                            materialUrl, materialFile, fileName, videoTimeStr, materialDescription);

                    int rowmod = moddao.moduleUpdateTime(moduleId);
                    int rowcou = coudao.courseUpdateTime(courseId);
                    if (res == 1) {
                        courseId = Integer.parseInt(course);
                        moduleId = Integer.parseInt(module);
                        Module m = mdao.getModuleByID(moduleId);
                        List<Material> listMaterial = madao.getAllMaterial(courseId, moduleId);
                        request.setAttribute("module", m);
                        request.setAttribute("listMaterial", listMaterial);
                        request.setAttribute("success", "Material created successfully!");
                        request.getRequestDispatcher("/WEB-INF/views/listMaterials.jsp").forward(request, response);

                    } else {
                        moduleId = Integer.parseInt(module);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "Failed to create material!!");
                        request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);

                    }
                } catch (Exception e) {
                    moduleId = Integer.parseInt(module);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Error failed to create material!!");
                    request.getRequestDispatcher("/WEB-INF/views/createMaterials.jsp").forward(request, response);

                }
            } else if (action.equalsIgnoreCase("delete")) {
                String idRaw = request.getParameter("id");
                String courseIdStr = request.getParameter("courseId");
                String moduleIdStr = request.getParameter("moduleId");
                int id = 0;
                try {
                    moduleId = Integer.parseInt(moduleIdStr);
                    courseId = Integer.parseInt(courseIdStr);
                    id = Integer.parseInt(idRaw);
                    if (madao.delete(id) == 1) {
                        courseId = Integer.parseInt(course);
                        moduleId = Integer.parseInt(module);
                        Module m = mdao.getModuleByID(moduleId);
                        List<Material> listMaterial = madao.getAllMaterial(courseId, moduleId);
                        request.setAttribute("module", m);
                        request.setAttribute("listMaterial", listMaterial);
                        request.setAttribute("success", "Material deleted successfully!");
                        request.getRequestDispatcher("/WEB-INF/views/listMaterials.jsp").forward(request, response);
                    } else {
                        courseId = Integer.parseInt(course);
                        moduleId = Integer.parseInt(module);
                        Module m = mdao.getModuleByID(moduleId);
                        List<Material> listMaterial = madao.getAllMaterial(courseId, moduleId);
                        request.setAttribute("module", m);
                        request.setAttribute("listMaterial", listMaterial);
                        request.setAttribute("success", "Failed to delete material!");
                        request.getRequestDispatcher("/WEB-INF/views/listMaterials.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    courseId = Integer.parseInt(course);
                    moduleId = Integer.parseInt(module);
                    Module m = mdao.getModuleByID(moduleId);
                    List<Material> listMaterial = madao.getAllMaterial(courseId, moduleId);
                    request.setAttribute("module", m);
                    request.setAttribute("listMaterial", listMaterial);
                    request.setAttribute("success", "Error failed to delete material!");
                    request.getRequestDispatcher("/WEB-INF/views/listMaterials.jsp").forward(request, response);
                }
            } else if (action.equalsIgnoreCase("update")) {
                String courseIdStr = request.getParameter("courseId");
                String moduleIdStr = request.getParameter("moduleId");
                String materialIdStr = request.getParameter("materialId");
                String materialName = request.getParameter("materialName");
                if (materialName == null || materialName.trim().isEmpty() || materialName.matches(".*\\s{2,}.*")) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material name must not contain consecutive spaces!");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    return;
                }

                if (materialName.length() > 255) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material name cannot exceed 255 letters!");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    return;
                }

                String type = request.getParameter("type");
                if (!type.equalsIgnoreCase("video") && !type.equalsIgnoreCase("pdf") && !type.equalsIgnoreCase("link")) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "The material type must be either video, PDF/Doc, or link — no other types are allowed.");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    return;
                }

                String materialOrderStr = request.getParameter("materialOrder");
                if (materialOrderStr == null || materialOrderStr.trim().isEmpty()) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material order must not be empty.");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    return;
                }
                int materialOrder;
                try {
                    materialOrder = Integer.parseInt(materialOrderStr.trim());
                    courseId = Integer.parseInt(courseIdStr);
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    // Tùy chọn: kiểm tra số âm
                    if (materialOrder < 0 || materialOrder > 10) {
                        moduleId = Integer.parseInt(module);
                        materialId = Integer.parseInt(material);
                        Material ma = madao.getMaterialById(materialId);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("material", ma);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "Material order must be a non-negative number.");
                        request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                        return;
                    }

                    boolean isExisted = madao.checkMaterialOrderExistsForUpdate(materialOrder, 
                            moduleId, courseId,materialId);

                    if (isExisted) {
                        moduleId = Integer.parseInt(module);
                        materialId = Integer.parseInt(material);
                        Material ma = madao.getMaterialById(materialId);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("material", ma);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "This Material Order already exists. Please choose a different number.");
                        request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                        return;
                    }
                    
                } catch (NumberFormatException e) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material order must be a valid integer.");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    return;
                }
                String materialDescription = request.getParameter("materialDescription");
                if (materialDescription == null || materialDescription.trim().isEmpty() || materialDescription.matches(".*\\s{2,}.*")) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Material description must not be empty!");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    return;
                }
                if (materialDescription != null) {
                    // Xóa khoảng trắng ở đầu mỗi dòn
                    materialDescription = Arrays.stream(materialDescription.split("\n"))
                            .map(String::stripLeading)
                            .collect(Collectors.joining("\n"));
                }
                InputStream materialFile = null;
                String fileName = null;
                String materialUrl = "";
                String videoTime = "";
                boolean updateNew = true;
                if ("video".equalsIgnoreCase(type)) {
                    materialUrl = request.getParameter("materialVideo");

                    videoTime = request.getParameter("videoTime");
                    if ("00:00:00".equals(videoTime) || videoTime == null || videoTime.trim().isEmpty()) {
                        moduleId = Integer.parseInt(module);
                        materialId = Integer.parseInt(material);
                        Material ma = madao.getMaterialById(materialId);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("material", ma);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "Video time cannot empty!");
                        request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                        return;
                    }
                    // Định dạng: hh:mm:ss hoặc mm:ss
                    String timePattern = "^((\\d{1,2}):)?([0-5]?\\d):([0-5]\\d)$";
                    if (!videoTime.matches(timePattern)) {
                        moduleId = Integer.parseInt(module);
                        materialId = Integer.parseInt(material);
                        Material ma = madao.getMaterialById(materialId);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("material", ma);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "Video time must be in format hh:mm:ss or mm:ss (e.g., 01:30 or 00:05:30).");
                        request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                        return;
                    }
                    // Kiểm tra và chuẩn hóa nếu cần
                    if (materialUrl == null || materialUrl.trim().isEmpty()) {
                        materialUrl = "";

                    }

                } else if ("link".equalsIgnoreCase(type)) {
                    materialUrl = request.getParameter("materialLink");
                    if (materialUrl == null || materialUrl.trim().isEmpty()) {
                        materialUrl = ""; // hoặc thông báo lỗi
                    }
                } else {

                    Part filePart = request.getPart("docFile");

                    materialFile = null;
                    fileName = null;
                    updateNew = true;
                    // Kiểm tra có upload file mới không
                    if (filePart != null && filePart.getSize() > 0) {
                        // Lấy stream và tên file mới
                        materialFile = filePart.getInputStream();

                        String contentDisp = filePart.getHeader("content-disposition");
                        for (String token : contentDisp.split(";")) {
                            if (token.trim().startsWith("filename")) {
                                fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                                break;
                            }
                        }
                    } else {
                        updateNew = false;
                    }
                }

                try {
                    moduleId = Integer.parseInt(moduleIdStr);
                    courseId = Integer.parseInt(courseIdStr);
                    materialId = Integer.parseInt(materialIdStr);
                    MaterialDAO dao = new MaterialDAO();
                    ModuleDAO moddao = new ModuleDAO();
                    CourseDAO coudao = new CourseDAO();
                    boolean res = dao.update(materialName, type, materialOrder, materialUrl,
                            materialFile, fileName,
                            videoTime, materialDescription, materialId, moduleId, courseId, updateNew);
                    int rowmod = moddao.moduleUpdateTime(moduleId);
                    int rowcou = coudao.courseUpdateTime(courseId);
                    if (res == true) {
                        courseId = Integer.parseInt(course);
                        moduleId = Integer.parseInt(module);
                        Module m = mdao.getModuleByID(moduleId);
                        List<Material> listMaterial = madao.getAllMaterial(courseId, moduleId);
                        request.setAttribute("module", m);
                        request.setAttribute("listMaterial", listMaterial);
                        request.setAttribute("success", "Material updated successfully!");
                        request.getRequestDispatcher("/WEB-INF/views/listMaterials.jsp").forward(request, response);
                    } else {
                        moduleId = Integer.parseInt(module);
                        materialId = Integer.parseInt(material);
                        Material ma = madao.getMaterialById(materialId);
                        Module mo = mdao.getModuleByID(moduleId);
                        request.setAttribute("material", ma);
                        request.setAttribute("module", mo);
                        request.setAttribute("err", "Failed to update material!");
                        request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    moduleId = Integer.parseInt(module);
                    materialId = Integer.parseInt(material);
                    Material ma = madao.getMaterialById(materialId);
                    Module mo = mdao.getModuleByID(moduleId);
                    request.setAttribute("material", ma);
                    request.setAttribute("module", mo);
                    request.setAttribute("err", "Error failed to update material!");
                    request.getRequestDispatcher("/WEB-INF/views/updateMaterials.jsp").forward(request, response);

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
