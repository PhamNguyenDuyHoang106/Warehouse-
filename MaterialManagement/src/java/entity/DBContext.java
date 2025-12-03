package entity;

import config.DatabaseConfig;
import utils.ConnectionManager;
import utils.ConnectionLeakDetector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database context for managing database connections.
 * Uses ConnectionManager for connection pooling to prevent "Too many connections" errors.
 * 
 * @author MaterialManagement Team
 */
public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static volatile DatabaseConfig dbConfig; // Lazy initialization to avoid ExceptionInInitializerError
    private static final boolean USE_CONNECTION_POOL = true; // Enable connection pooling

    protected Connection connection;
    private boolean fromPool = false; // Track if connection came from pool

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
     * Establish database connection using ConnectionManager (pool) or direct connection.
     * Throws RuntimeException if connection fails to ensure connection is never null.
     */
    private void connect() {
        try {
            if (connection == null || (connection != null && connection.isClosed())) {
                if (USE_CONNECTION_POOL) {
                    // Use connection pool
                    try {
                        ConnectionManager pool = ConnectionManager.getInstance();
                        connection = pool.getConnection();
                        fromPool = true;
                        LOGGER.log(Level.FINE, "Connection obtained from pool");
                    } catch (SQLException e) {
                        LOGGER.log(Level.WARNING, "Failed to get connection from pool, falling back to direct connection", e);
                        // Fallback to direct connection
                        fromPool = false;
                        createDirectConnection();
                    }
                } else {
                    // Direct connection (legacy mode)
                    fromPool = false;
                    createDirectConnection();
                }
            }
        } catch (Exception ex) {
            DatabaseConfig config = getDbConfig();
            String errorMsg = String.format(
                "Failed to connect to database: %s. URL: %s, User: %s", 
                ex.getMessage(), 
                config != null ? config.getConnectionUrl() : "unknown", 
                config != null ? config.getUsername() : "unknown"
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
     * Create a direct database connection (fallback method)
     */
    private void createDirectConnection() throws SQLException, ClassNotFoundException {
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
            "Attempting direct database connection to {0} as user {1}", 
            new Object[]{config.getDatabaseName(), username});
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);
        LOGGER.log(Level.INFO, 
            "Direct database connection established successfully to {0}", 
            config.getDatabaseName());
    }

    /**
     * Get database connection, ensuring it's not null and valid.
     * Reconnects if connection is null or closed.
     * Updates leak detector access time.
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
            
            // Update leak detector access time
            ConnectionLeakDetector.getInstance().updateAccess(connection);
            
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking connection: " + e.getMessage(), e);
            // Try to reconnect
            try {
                connect();
                if (connection == null) {
                    throw new RuntimeException("Failed to reconnect to database", e);
                }
                ConnectionLeakDetector.getInstance().updateAccess(connection);
                return connection;
            } catch (Exception ex) {
                throw new RuntimeException("Critical: Database connection unavailable", ex);
            }
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                if (fromPool && USE_CONNECTION_POOL) {
                    // Return connection to pool
                    ConnectionManager.getInstance().returnConnection(connection);
                    LOGGER.log(Level.FINE, "Connection returned to pool");
                } else {
                    // Close direct connection
                    connection.close();
                    LOGGER.log(Level.FINE, "Direct connection closed");
                }
                connection = null;
                fromPool = false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing connection: " + e.getMessage(), e);
            // Try to close directly if pool return failed
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                // Ignore
            }
        }
    }

    public static void main(String[] args) {
        new DBContext();
    }
}
