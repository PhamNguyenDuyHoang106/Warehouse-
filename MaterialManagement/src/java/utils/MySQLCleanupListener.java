package utils;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class MySQLCleanupListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(MySQLCleanupListener.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "🧹 Cleaning up JDBC drivers and threads...");

        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            LOGGER.log(Level.INFO, "AbandonedConnectionCleanupThread shutdown successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error shutting down AbandonedConnectionCleanupThread: " + e.getMessage(), e);
        }

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                LOGGER.log(Level.INFO, "✅ Deregistered JDBC driver: " + driver);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deregistering JDBC driver " + driver.getClass().getName() + ": " + e.getMessage(), e);
            }
        }

        LOGGER.log(Level.INFO, "✅ MySQL cleanup completed!");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "🚀 MySQLCleanupListener initialized");
    }
}
