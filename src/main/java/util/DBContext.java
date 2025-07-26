package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ngo Phuoc Thinh - CE170008 - SE1815
 */
public class DBContext {

    public Connection conn = null;

    // Connect Local
//    public DBContext() {
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            String dbURL = "jdbc:sqlserver://localhost:1433;"
//                    + "databaseName=FLearn;"
//                    + "user=sa;"
//                    + "password=123456;"
//                    + "encrypt=false;trustServerCertificate=true;loginTimeout=30;";
//            conn = DriverManager.getConnection(dbURL);
//
//            if (conn != null) {
//                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
//                System.out.println("Driver name: " + dm.getDriverName());
//                System.out.println("Driver version: " + dm.getDriverVersion());
//                System.out.println("Product name: "
//                        + dm.getDatabaseProductName());
//                System.out.println("Product version: "
//                        + dm.getDatabaseProductVersion());
//            }
//        } catch (SQLException ex) {
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    private static HikariDataSource ds;
    
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlserver://148.230.96.68:1433;databaseName=FLearn;trustServerCertificate=true;");
            config.setUsername("sa");
            config.setPassword("Group3-FPTU-NPT");
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setValidationTimeout(5000);
            config.setLeakDetectionThreshold(15000);

            ds = new HikariDataSource(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public DBContext() {
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void closePool() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }
}
