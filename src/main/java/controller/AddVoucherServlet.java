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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Role;
import model.User;

@WebServlet(name = "AddVoucherServlet", urlPatterns = {"/addVoucher"})
public class AddVoucherServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddVoucherServlet.class.getName());

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
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute("voucher", new Voucher());
        request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
    }

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

        String voucherName = request.getParameter("voucherName");
        String voucherCode = request.getParameter("voucherCode");
        String expiredDateStr = request.getParameter("expiredDate");
        String saleType = request.getParameter("saleType");
        String saleAmountStr = request.getParameter("saleAmount");
        String minPriceStr = request.getParameter("minPrice");
        String amountStr = request.getParameter("amount");

        Timestamp expiredDate = null;
        int saleAmount = 0;
        int minPrice = 0;
        int amount = 0;

        if (voucherName == null || voucherName.trim().isEmpty()) {
            errorMessages.add("Not null here.");
        }

        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            errorMessages.add("Not null here.");
        }

        if (expiredDateStr == null || expiredDateStr.trim().isEmpty()) {
            errorMessages.add("Not null here.");
        } else {
            try {
                LocalDateTime inputDateTime = LocalDateTime.parse(expiredDateStr);
                expiredDate = Timestamp.valueOf(inputDateTime);
                if (inputDateTime.isBefore(LocalDateTime.now())) {
                    errorMessages.add("Expired date must be in the future.");
                }
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Invalid Expiration Date format: " + expiredDateStr, e);
                errorMessages.add("Date: Wrong format. Use YYYY-MM-DDTHH:MM.");
            }
        }

        if (saleType == null || saleType.trim().isEmpty()) {
            errorMessages.add("Not null here.");
        }

        if (saleAmountStr == null || saleAmountStr.trim().isEmpty()) {
            errorMessages.add("Not null here.");
        } else {
            try {
                saleAmount = Integer.parseInt(saleAmountStr.trim());
                if (saleAmount <= 0) {
                    errorMessages.add("The discount amount must be greater than 0.");
                }
                if (saleType != null && saleType.equals("PERCENT") && (saleAmount > 100 || saleAmount < 0)) {
                    errorMessages.add("For percentage, discount amount must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Invalid discount amount (must be an integer).");
            }
        }

        if (minPriceStr == null || minPriceStr.trim().isEmpty()) {
            errorMessages.add("Not null here.");
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
            errorMessages.add("Not null here.");
        } else {
            try {
                amount = Integer.parseInt(amountStr.trim());
                if (amount <= 0) {
                    errorMessages.add("Quantity must be > 0.");
                }
            } catch (NumberFormatException e) {
                errorMessages.add("Invalid quantity (must be an integer).");
            }
        }

        VoucherDAO voucherDAO = new VoucherDAO();
        try {
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                if (voucherDAO.isVoucherCodeExists(voucherCode.trim())) { 
                    errorMessages.add("Voucher code '" + voucherCode.trim() + "' already exists. Please select another code..");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error.", ex);
            errorMessages.add("Error.");
        }

        if (!errorMessages.isEmpty()) {
            globalMessage = "Update failed. Please check and fix all of these errors:.";
            request.setAttribute("globalMessage", globalMessage);
            request.setAttribute("err", errorMessages);
            request.setAttribute("errorGlobalMessage", true);

            Voucher voucherForDisplay = new Voucher();
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

        Voucher newVoucher = new Voucher();
        newVoucher.setVoucherName(voucherName.trim());
        newVoucher.setVoucherCode(voucherCode.trim());
        newVoucher.setExpiredDate(expiredDate);
        newVoucher.setSaleType(saleType.trim());
        newVoucher.setSaleAmount(saleAmount);
        newVoucher.setMinPrice(minPrice);
        newVoucher.setAmount(amount);

        try {
            boolean success = voucherDAO.addVoucher(newVoucher);
            if (success) {
                request.getSession().setAttribute("success", "Voucher added successfully!");
                response.sendRedirect(request.getContextPath() + "/voucherList");
            } else {
                globalMessage = "Add new Voucher failed. An error occurred..";
                request.setAttribute("globalMessage", globalMessage);
                request.setAttribute("errorGlobalMessage", true);
                request.setAttribute("voucher", newVoucher);
                request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error", ex);
            globalMessage = "Error: " + ex.getMessage();
            request.setAttribute("globalMessage", globalMessage);
            request.setAttribute("errorGlobalMessage", true);
            request.setAttribute("voucher", newVoucher);
            request.getRequestDispatcher("/WEB-INF/views/voucherDetails.jsp").forward(request, response);
        }
    }
}
