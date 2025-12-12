package utils;

import dal.UserDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class CleanupScheduler implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(CleanupScheduler.class.getName());
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            
            scheduler.scheduleAtFixedRate(() -> {
                // Cleanup expired users
                UserDAO userDAO = null;
                try {
                    userDAO = new UserDAO();
                    
                    try {
                        java.sql.Connection conn = userDAO.getConnection();
                        if (conn == null) {
                            return;
                        }
                        
                        String sql = 
                            "DELETE FROM Users " +
                            "WHERE deleted_at IS NOT NULL " +
                            "AND deleted_at < DATE_SUB(NOW(), INTERVAL 90 DAY)";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.executeUpdate();
                        }
                    } catch (java.sql.SQLException sqlEx) {
                        LOGGER.log(Level.WARNING, "Database connection error, skipping cleanup", sqlEx);
                        return;
                    }
                } catch (RuntimeException e) {
                    if (e.getMessage() != null && 
                        (e.getMessage().contains("password not configured") || 
                         e.getMessage().contains("Database password not configured") ||
                         e.getMessage().contains("Failed to initialize DatabaseConfig"))) {
                        LOGGER.log(Level.FINE, "Database not configured yet, skipping cleanup");
                    } else {
                        LOGGER.log(Level.SEVERE, "Error deleting expired accounts", e);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error deleting expired accounts", e);
                } finally {
                    if (userDAO != null) {
                        try {
                            userDAO.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error closing connection", e);
                        }
                    }
                }
                
                // Cleanup expired sessions
                dal.SessionDAO sessionDAO = null;
                try {
                    sessionDAO = new dal.SessionDAO();
                    int cleaned = sessionDAO.cleanupExpired();
                    if (cleaned > 0) {
                        LOGGER.log(Level.FINE, "Cleaned up " + cleaned + " expired sessions");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error cleaning up expired sessions", e);
                } finally {
                    if (sessionDAO != null) {
                        try {
                            sessionDAO.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error closing sessionDAO", e);
                        }
                    }
                }
                
                // Cleanup old rate limit records
                dal.RateLimitDAO rateLimitDAO = null;
                try {
                    rateLimitDAO = new dal.RateLimitDAO();
                    int cleaned = rateLimitDAO.cleanupOldRecords(60); // Keep last 60 minutes
                    if (cleaned > 0) {
                        LOGGER.log(Level.FINE, "Cleaned up " + cleaned + " old rate limit records");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error cleaning up old rate limit records", e);
                } finally {
                    if (rateLimitDAO != null) {
                        try {
                            rateLimitDAO.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error closing rateLimitDAO", e);
                        }
                    }
                }
                
                // Cleanup old IP rate limit records
                dal.IPRateLimitDAO ipRateLimitDAO = null;
                try {
                    ipRateLimitDAO = new dal.IPRateLimitDAO();
                    int cleaned = ipRateLimitDAO.cleanupOldRecords(1); // Keep last 1 day
                    if (cleaned > 0) {
                        LOGGER.log(Level.FINE, "Cleaned up " + cleaned + " old IP rate limit records");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error cleaning up old IP rate limit records", e);
                } finally {
                    if (ipRateLimitDAO != null) {
                        try {
                            ipRateLimitDAO.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error closing ipRateLimitDAO", e);
                        }
                    }
                }
            }, 5, 60, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize CleanupScheduler", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }
}