/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Role;
import model.User;

@WebServlet(name = "DeleteVoucherServlet", urlPatterns = {"/deleteVoucher"})
public class DeleteVoucherServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DeleteVoucherServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        Role role = user.getRole();
        if (role != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        List<String> errorMessages = new ArrayList<>();
        String globalMessage = "";

        String action = request.getParameter("action");
        String voucherIDStr = request.getParameter("voucherID");

        VoucherDAO voucherDAO = new VoucherDAO();
        boolean success = false;

        try {
            if ("deleteExpired".equals(action)) {
                success = voucherDAO.deleteExpiredVouchers();
                if (success) {
                    globalMessage = "Successfully deleted all expired vouchers.";
                    request.setAttribute("success", globalMessage);
                } else {
                    globalMessage = "No expired vouchers were found to delete.";
                    errorMessages.add(globalMessage);
                    request.setAttribute("err", errorMessages);
                }
            } else if (voucherIDStr != null && !voucherIDStr.trim().isEmpty()) {
                try {
                    int voucherID = Integer.parseInt(voucherIDStr.trim());
                    success = voucherDAO.deleteVoucher(voucherID);
                    if (success) {
                        globalMessage = "Delete Voucher " + voucherID + " succeeded!";
                        request.setAttribute("success", globalMessage);
                    } else {
                        globalMessage = "Delete failed: Voucher " + voucherID + " not found.";
                        errorMessages.add(globalMessage);
                        request.setAttribute("err", errorMessages);
                    }
                } catch (NumberFormatException e) {
                    globalMessage = "Voucher ID must be an integer.";
                    errorMessages.add(globalMessage);
                    request.setAttribute("err", errorMessages);
                }
            } else {
                globalMessage = "Missing or invalid Voucher ID.";
                errorMessages.add(globalMessage);
                request.setAttribute("err", errorMessages);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error deleting voucher", ex);
            globalMessage = "Database error: " + ex.getMessage();
            errorMessages.add(globalMessage);
            request.setAttribute("err", errorMessages);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected error deleting voucher", ex);
            globalMessage = "Unexpected error occurred.";
            errorMessages.add(globalMessage);
            request.setAttribute("err", errorMessages);
        }

        request.getRequestDispatcher("/voucherList").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        Role role = user.getRole();
        if (role != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        doPost(request, response);
    }
}
