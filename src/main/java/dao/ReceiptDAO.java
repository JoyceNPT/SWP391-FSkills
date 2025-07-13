package dao;

import model.Receipt;
import util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    
    public ArrayList<Receipt> getLearnerReceipt(int UserID){
        ArrayList<Receipt> receiptList = new ArrayList<Receipt>();
        String sql = "Select * From Receipts Where UserID = ? AND PaymentStatus = 1";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, UserID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                receiptList.add(new Receipt(rs.getInt("ReceiptID"), rs.getInt("UserID"),  rs.getString("PaymentDetail"), rs.getInt("PaymentStatus"), rs.getTimestamp("PaymentDate")));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receiptList;
    }
}