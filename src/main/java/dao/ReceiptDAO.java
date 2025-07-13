package dao;

import model.Receipt;
import util.DBContext;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiptDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ReceiptDAO.class.getName());

    public boolean saveReceipt(Receipt receipt) throws SQLException {
        String sql = "INSERT INTO Receipts (UserID, PaymentDetail, PaymentStatus, PaymentDate) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receipt.getUserID());
            ps.setString(2, receipt.getPaymentDetail());
            ps.setInt(3, receipt.getPaymentStatus());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in saveReceipt: " + e.getMessage(), e);
            throw e;
        }
    }
}