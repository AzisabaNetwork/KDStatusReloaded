package net.azisaba.kdstatusreloaded.task;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class DBConnectionCheckTask extends BukkitRunnable {
    private final KDStatusReloaded plugin;

    @Override
    public void run() {
        try (Connection conn = plugin.sql.getConnection()) {
            if (conn == null) {
                plugin.sql.connect();
            }
            try (Connection con = plugin.sql.getConnection();
                 PreparedStatement pstmt = con.prepareStatement("SELECT 1")) {
                if (pstmt.executeQuery().next()) {
                    if (plugin.getPluginConfig().showLogInConsole) {
                        plugin.getLogger().info("SQL Connection is alive!");
                    }
                    return;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to pass health check", e);
        }
        plugin.getLogger().info("Reconnecting to database...");
        plugin.sql.reconnect();
        plugin.getLogger().info("Successfully to reconnect database!");
    }
}
