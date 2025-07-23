/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.VoucherDAO;
import model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Role;
import model.User;

@WebServlet(name = "UpdateVoucherServlet", urlPatterns = {"/updateVoucher"})
public class UpdateVoucherServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UpdateVoucherServlet.class.getName());

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

        List<String> errorMessages = new ArrayList();
        String globalMessage = "";

        String voucherIDStr = request.getParameter("voucherID");
        String voucherName = request.getParameter("voucherName");
        String voucherCode = request.getParameter("voucherCode");
        String expiredDateStr = request.getParameter("expiredDate");
        String saleType = request.getParameter("saleType");
        String saleAmountStr = request.getParameter("saleAmount");
        String minPriceStr = request.getParameter("minPrice");
        String amountStr = request.getParameter("amount");

        int voucherID = 0;
        Timestamp expiredDate = null;
        int saleAmount = 0;
        int minPrice = 0;
        int amount = 0;

        if (voucherIDStr == null || voucherIDStr.trim().isEmpty()) {
            errorMessages.add("Voucher ID can not be null.");
        } else {
            try {
                voucherID = Integer.parseInt(voucherIDStr.trim());
            } catch (NumberFormatException e) {
                errorMessages.add("Voucher ID must be an integer.");
            }
        }
        
        if (voucherName == null || voucherName.trim().isEmpty()) {
            errorMessages.add("Voucher name cannot be empty.");
        }
        
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            errorMessages.add("Voucher code cannot be empty.");
        }

        if (expiredDateStr == null || expiredDateStr.trim().isEmpty()) {
            errorMessages.add("Please enter this value.");
        } else {
            try {
                LocalDateTime inaddDateTime = LocalDateTime.parse(expiredDateStr);
                expiredDate = Timestamp.valueOf(inaddDateTime); 
                if (inaddDateTime.isBefore(LocalDateTime.now())) {
                    errorMessages.add("The expired day must be in the future.");
                }
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Invalid Expiration Date format: " + expiredDateStr, e);
                errorMessages.add("Invalid date format.");
            }
        }

        if (saleType == null || saleType.trim().isEmpty()) {
            errorMessages.add("Sale type: Please enter for this value.");
        } else if (!saleType.equals("PERCENT") && !saleType.equals("FIXED")) {
            errorMessages.add("Sale type: Not valid sale.");
        }

        if (saleAmountStr == null || saleAmountStr.trim().isEmpty()) {
            errorMessages.add("Sale amount: Not null here.");
        } else {
            try {
                saleAmount = Integer.parseInt(saleAmountStr.trim());
                if (saleAmount <= 0) {
                    errorMessages.add("Sale Amount Must be >0.");
                }
                if (saleType != null && saleType.equals("PERCENT") && (saleAmount > 100 || saleAmount < 0)) {
                    errorMessages.add("For percent input 1-100.");
                }
                if (saleType != null && saleType.equals("FIXED") && saleAmount < 1000) {
                    errorMessages.add("For fixed price please input >= 1000.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Sale Type: Must be an integer.");
            }
        }

        if (minPriceStr == null || minPriceStr.trim().isEmpty()) {
            errorMessages.add("Minimum price cannot be empty.");
        } else {
            try {
                minPrice = Integer.parseInt(minPriceStr.trim());
                if (minPrice < 0) {
                    errorMessages.add("Minimum price must be non-negative.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Invalid minimum price (must be an integer).");
            }
        }
       
        if (amountStr == null || amountStr.trim().isEmpty()) {
            errorMessages.add("Amount cannot be empty.");
        } else {
            try {
                amount = Integer.parseInt(amountStr.trim());
                if (amount <= 0) {
                    errorMessages.add("Amount must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Invalid amount (must be an integer).");
            }
        }
        
        VoucherDAO voucherDAO = new VoucherDAO();
        try {
            Voucher originalVoucher = voucherDAO.getVoucherByID(voucherID); 
            
            if (originalVoucher != null && !originalVoucher.getVoucherCode().equals(voucherCode.trim())) {
                if (voucherDAO.isVoucherCodeExistsForOtherVoucher(voucherCode.trim(), voucherID)) {
                    errorMessages.add("Updated Failed: Voucher code '" + voucherCode.trim() + "' already exists for another voucher. Please choose a different code.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error checking for duplicate voucher code during update.", ex);
            errorMessages.add("An error occurred while checking voucher code existence. Please try again.");
        }

        if (!errorMessages.isEmpty()) {
            globalMessage = "Voucher update failed. Please check for errors.";
            request.setAttribute("err", globalMessage);
            request.setAttribute("err", errorMessages);

            Voucher voucherForDisplay = new Voucher();
            voucherForDisplay.setVoucherID(voucherID);
            voucherForDisplay.setVoucherName(voucherName);
            voucherForDisplay.setVoucherCode(voucherCode);
            voucherForDisplay.setExpiredDate(expiredDate);
            voucherForDisplay.setSaleType(saleType);
            voucherForDisplay.setSaleAmount(saleAmount);
            voucherForDisplay.setMinPrice(minPrice);
            voucherForDisplay.setAmount(amount);

            request.setAttribute("voucher", voucherForDisplay);

            request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
            return;
        }

        Voucher updatedVoucher = new Voucher();
        updatedVoucher.setVoucherID(voucherID);
        updatedVoucher.setVoucherName(voucherName.trim());
        updatedVoucher.setVoucherCode(voucherCode.trim());
        updatedVoucher.setExpiredDate(expiredDate); 
        updatedVoucher.setSaleType(saleType.trim());
        updatedVoucher.setSaleAmount(saleAmount);
        updatedVoucher.setMinPrice(minPrice);
        updatedVoucher.setAmount(amount);

        try {
            boolean success = voucherDAO.updateVoucher(updatedVoucher);
            if (success) {
                request.getSession().setAttribute("success", "Voucher updated successfully!");
                request.setAttribute("successMessage", true);
                response.sendRedirect(request.getContextPath() + "/voucherList");
            } else {
                globalMessage = "Voucher update failed. Voucher not found or no changes made.";
                request.setAttribute("err", globalMessage);
                request.setAttribute("err", true);
                request.setAttribute("voucher", updatedVoucher);
                request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error updating voucher", ex);
            globalMessage = "Database error: " + ex.getMessage();
            request.setAttribute("err", globalMessage);
            request.setAttribute("err", true);

            request.setAttribute("voucher", updatedVoucher);
            request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
        }
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
        String voucherIDParam = request.getParameter("voucherID");
        VoucherDAO voucherDAO = new VoucherDAO();
        Voucher voucher = null;
        String globalMessage = "";

        if (voucherIDParam != null && !voucherIDParam.isEmpty()) {
            try {
                int voucherID = Integer.parseInt(voucherIDParam);
                voucher = voucherDAO.getVoucherByID(voucherID);
                if (voucher == null) {
                    globalMessage = "Voucher with ID " + voucherID + " not found.";
                }
            } catch (NumberFormatException e) {
                globalMessage = "Invalid Voucher ID format.";
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Database error retrieving voucher.", ex);
                globalMessage = "Database error: " + ex.getMessage();
            }
        }

        request.setAttribute("voucher", voucher);
        request.setAttribute("globalMessage", globalMessage);
        request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
    }
}