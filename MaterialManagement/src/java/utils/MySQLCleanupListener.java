package utils;

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
        try {
            Class<?> cleanupThreadClass = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            java.lang.reflect.Method shutdownMethod = cleanupThreadClass.getMethod("checkedShutdown");
            shutdownMethod.invoke(null);
        } catch (ClassNotFoundException e) {
            // MySQL connector classes not found
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Error accessing AbandonedConnectionCleanupThread method", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            String errorMsg = cause != null ? cause.getMessage() : e.getMessage();
            LOGGER.log(Level.WARNING, "Error invoking AbandonedConnectionCleanupThread shutdown: " + errorMsg);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error shutting down AbandonedConnectionCleanupThread", e);
        }

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deregistering JDBC driver " + driver.getClass().getName(), e);
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }
}
