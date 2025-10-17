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
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        scheduler.scheduleAtFixedRate(() -> {
            UserDAO userDAO = new UserDAO();
            String sql = "DELETE FROM Users WHERE status = 'inactive' AND verification_status = 'pending' AND verification_expiry < CURRENT_TIMESTAMP";
            try (PreparedStatement ps = userDAO.getConnection().prepareStatement(sql)) {
                int rowsAffected = ps.executeUpdate();
                System.out.println("✅ Đã xóa hoàn toàn " + rowsAffected + " tài khoản hết hạn khỏi cơ sở dữ liệu");
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi xóa tài khoản hết hạn: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.SECONDS); 
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