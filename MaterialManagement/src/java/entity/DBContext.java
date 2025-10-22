package entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Hoang1062004";

    protected Connection connection;

    public DBContext() {
        connect();
    }

    private void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LOGGER.log(Level.INFO, "✅ Kết nối thành công tới MySQL!");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "❌ Lỗi kết nối MySQL: " + ex.getMessage(), ex);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Lỗi khi kiểm tra kết nối: " + e.getMessage(), e);
        }
        return connection;
    }

    public static void main(String[] args) {
        new DBContext();
    }
}
