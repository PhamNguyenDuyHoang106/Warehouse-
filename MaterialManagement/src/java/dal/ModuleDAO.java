package dal;

import entity.DBContext;
import entity.Module;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModuleDAO extends DBContext {

    public List<Module> getAllModules() {
        List<Module> moduleList = new ArrayList<>();
        String sql = "SELECT * FROM Modules WHERE disable = 0 ORDER BY module_id";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Module module = new Module();
                module.setModuleId(rs.getInt("module_id"));
                module.setModuleName(rs.getString("module_name"));
                module.setDescription(rs.getString("description"));
                moduleList.add(module);
            }
            System.out.println("✅ Lấy danh sách module thành công, số lượng: " + moduleList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllModules: " + e.getMessage());
            e.printStackTrace();
        }
        return moduleList;
    }
}