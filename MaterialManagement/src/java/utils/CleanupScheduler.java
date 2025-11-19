package utils;

import dal.UserDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.sql.PreparedStatement;

@WebListener
public class CleanupScheduler implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            
            // Cleanup every 1 hour instead of 30 seconds to reduce database load
            // Delay initial execution by 5 minutes to allow database to be ready
            scheduler.scheduleAtFixedRate(() -> {
                UserDAO userDAO = null;
                try {
                    // Test database connection before attempting cleanup
                    userDAO = new UserDAO();
                    
                    // Verify connection is valid (getConnection() already validates)
                    try {
                        java.sql.Connection conn = userDAO.getConnection();
                        if (conn == null) {
                            System.err.println("⚠️ Database connection not available, skipping cleanup");
                            return;
                        }
                        
                        String sql = 
                            "DELETE FROM Users " +
                            "WHERE deleted_at IS NOT NULL " +
                            "AND deleted_at < DATE_SUB(NOW(), INTERVAL 90 DAY)";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            int rowsAffected = ps.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("✅ Đã xóa hoàn toàn " + rowsAffected + " tài khoản hết hạn khỏi cơ sở dữ liệu");
                            }
                        }
                    } catch (java.sql.SQLException sqlEx) {
                        System.err.println("⚠️ Database connection error, skipping cleanup: " + sqlEx.getMessage());
                        return;
                    }
                } catch (RuntimeException e) {
                    // Handle configuration errors (database not configured)
                    if (e.getMessage() != null && 
                        (e.getMessage().contains("password not configured") || 
                         e.getMessage().contains("Database password not configured") ||
                         e.getMessage().contains("Failed to initialize DatabaseConfig"))) {
                        System.err.println("⚠️ Database not configured yet, skipping cleanup. Error: " + e.getMessage());
                    } else {
                        System.err.println("❌ Lỗi khi xóa tài khoản hết hạn: " + e.getMessage());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi xóa tài khoản hết hạn: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // IMPORTANT: Close connection to prevent leak
                    if (userDAO != null) {
                        try {
                            userDAO.close();
                        } catch (Exception e) {
                            System.err.println("❌ Lỗi khi đóng kết nối: " + e.getMessage());
                        }
                    }
                }
            }, 5, 60, TimeUnit.MINUTES); // Delay 5 minutes initial, then run every 60 minutes
            
            System.out.println("✅ CleanupScheduler initialized successfully");
        } catch (Exception e) {
            // Don't let scheduler initialization failure crash the application
            System.err.println("❌ Failed to initialize CleanupScheduler: " + e.getMessage());
            e.printStackTrace();
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