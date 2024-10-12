package jp.azisaba.lgw.kdstatus.task;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class DBConnectionCheckTask extends BukkitRunnable {
    private final KDStatusReloaded plugin;
    @Override
    public void run() {
        try {
            if(plugin.sql.getConnection() == null) {
                plugin.sql.connect();
            }
            PreparedStatement pstmt = plugin.sql.getConnection().prepareStatement("SELECT 1");
            if(pstmt.executeQuery().next()) {
                if(plugin.getPluginConfig().showLogInConsole) {
                    plugin.getLogger().info("SQL Connection is alive!");
                }
                pstmt.close();
                return;
            }
            pstmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to pass health check", e);
        }
        plugin.getLogger().info("Reconnecting to database...");
        try {
            plugin.sql.reconnect();
            plugin.getLogger().info("Successfully to reconnect database!");
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reconnect database", ex);
        }
    }
}
