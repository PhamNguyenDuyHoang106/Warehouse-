package entity;

import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database context for managing database connections.
 * Uses DatabaseConfig to read credentials from properties file or environment variables.
 * 
 * @author MaterialManagement Team
 */
public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static volatile DatabaseConfig dbConfig; // Lazy initialization to avoid ExceptionInInitializerError

    protected Connection connection;

    public DBContext() {
        connect();
    }
    
    /**
     * Get DatabaseConfig instance with lazy initialization.
     * This avoids ExceptionInInitializerError if database is not configured at class load time.
     */
    private static DatabaseConfig getDbConfig() {
        if (dbConfig == null) {
            synchronized (DBContext.class) {
                if (dbConfig == null) {
                    try {
                        dbConfig = DatabaseConfig.getInstance();
                    } catch (RuntimeException e) {
                        LOGGER.log(Level.SEVERE, 
                            "Failed to initialize DatabaseConfig: {0}", 
                            e.getMessage());
                        throw e;
                    }
                }
            }
        }
        return dbConfig;
    }

    /**
     * Establish database connection using configuration from DatabaseConfig.
     * Throws RuntimeException if connection fails to ensure connection is never null.
     */
    private void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                // Get config with lazy initialization
                DatabaseConfig config = getDbConfig();
                
                // Validate configuration before attempting connection
                String username = config.getUsername();
                String password = config.getPassword();
                String url = config.getConnectionUrl();
                
                if (username == null || username.isEmpty()) {
                    throw new RuntimeException("Database username is not configured!");
                }
                if (password == null || password.isEmpty()) {
                    throw new RuntimeException("Database password is not configured! Check database.properties or DB_PASSWORD environment variable.");
                }
                if (url == null || url.isEmpty()) {
                    throw new RuntimeException("Database connection URL is not configured!");
                }
                
                LOGGER.log(Level.INFO, 
                    "Attempting database connection to {0} as user {1} (password: {2})", 
                    new Object[]{config.getDatabaseName(), username, password.isEmpty() ? "NOT SET" : "SET"});
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                    url,
                    username,
                    password
                );
                LOGGER.log(Level.INFO, 
                    "Database connection established successfully to {0}", 
                    config.getDatabaseName());
            }
        } catch (ClassNotFoundException ex) {
            String errorMsg = "MySQL JDBC Driver not found. Please ensure mysql-connector-j is in classpath.";
            LOGGER.log(Level.SEVERE, errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
        } catch (SQLException ex) {
            DatabaseConfig config = getDbConfig();
            String errorMsg = String.format(
                "Failed to connect to database: %s. URL: %s, User: %s", 
                ex.getMessage(), 
                config.getConnectionUrl(), 
                config.getUsername()
            );
            LOGGER.log(Level.SEVERE, errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
        }
        
        // Ensure connection is not null after connect attempt
        if (connection == null) {
            String errorMsg = "Database connection is null after connection attempt. Check database configuration.";
            LOGGER.log(Level.SEVERE, errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * Get database connection, ensuring it's not null and valid.
     * Reconnects if connection is null or closed.
     * 
     * @return Connection object, never null
     * @throws RuntimeException if connection cannot be established
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            
            // Double check after reconnect attempt
            if (connection == null) {
                String errorMsg = "Failed to establish database connection. Check database configuration and server status.";
                LOGGER.log(Level.SEVERE, errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking connection: " + e.getMessage(), e);
            // Try to reconnect
            try {
                connect();
                if (connection == null) {
                    throw new RuntimeException("Failed to reconnect to database", e);
                }
                return connection;
            } catch (Exception ex) {
                throw new RuntimeException("Critical: Database connection unavailable", ex);
            }
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.log(Level.INFO, "✅ Đã đóng kết nối MySQL");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Lỗi khi đóng kết nối: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        new DBContext();
    }
}
