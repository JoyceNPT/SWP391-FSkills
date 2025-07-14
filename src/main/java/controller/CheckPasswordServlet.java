package controller;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "CheckPasswordServlet", urlPatterns = {"/checkPassword"})
public class CheckPasswordServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.getWriter().write("{\"valid\": false, \"message\": \"User not logged in\"}");
            return;
        }
        
        String oldPassword = request.getParameter("oldPassword");
        
        try {
            UserDAO userDAO = new UserDAO();
            boolean isPasswordCorrect = userDAO.checkPassword(user.getUserId(), oldPassword);
            
            if (isPasswordCorrect) {
                response.getWriter().write("{\"valid\": true}");
            } else {
                response.getWriter().write("{\"valid\": false}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"valid\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}