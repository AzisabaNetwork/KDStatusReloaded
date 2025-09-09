package net.azisaba.kdstatusreloaded;

import net.azisaba.kdstatusreloaded.commands.KDSCommands;
import net.azisaba.kdstatusreloaded.commands.KDStatusCommand;
import net.azisaba.kdstatusreloaded.commands.MyStatusCommand;
import net.azisaba.kdstatusreloaded.listeners.JoinQuitListener;
import net.azisaba.kdstatusreloaded.listeners.KDSListeners;
import net.azisaba.kdstatusreloaded.listeners.KillDeathListener;
import net.azisaba.kdstatusreloaded.sql.DBAuthConfig;
import net.azisaba.kdstatusreloaded.sql.HikariMySQLDatabase;
import net.azisaba.kdstatusreloaded.sql.KillDeathDataContainer;
import net.azisaba.kdstatusreloaded.sql.PlayerDataMySQLController;
import net.azisaba.kdstatusreloaded.sql.PlayerDataSQLController;
import net.azisaba.kdstatusreloaded.sql.SQLHandler;
import net.azisaba.kdstatusreloaded.task.DBConnectionCheckTask;
import net.azisaba.kdstatusreloaded.task.SavePlayerDataTask;
import net.azisaba.kdstatusreloaded.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class KDStatusReloaded extends JavaPlugin {

    private KDStatusConfig pluginConfig;
    private KillDeathDataContainer kdDataContainer;
    private static KDStatusReloaded plugin;
    private SavePlayerDataTask saveTask;
    private DBConnectionCheckTask dbCheckTask;

    private SQLHandler sqlHandler = null;

    public HikariMySQLDatabase sql;

    private PlayerDataMySQLController kdData;

    @Override
    public void onEnable() {

        plugin = this;

        getConfig().addDefault("migrated", false);
        getConfig().addDefault("host", "localhost");
        getConfig().addDefault("port", 3306);
        getConfig().addDefault("database", "kdstatusreloaded");
        getConfig().addDefault("username", "root");
        getConfig().addDefault("password", "password");
        getConfig().options().copyDefaults(true);
        saveConfig();

        pluginConfig = new KDStatusConfig(this);

        sqlHandler = new SQLHandler(new File(getDataFolder(), "playerData.db"));
        kdDataContainer = new KillDeathDataContainer(new PlayerDataSQLController(sqlHandler).init());

        DBAuthConfig.loadAuthConfig();
        sql = DBAuthConfig.getDatabase(getLogger(), 10);
        sql.connect();

        this.kdData = new PlayerDataMySQLController(sql, getLogger());
        this.kdData.init();

        saveTask = new SavePlayerDataTask(this);
        saveTask.runTaskTimerAsynchronously(this, 20 * 60 * 3, 20 * 60 * 3);

        dbCheckTask = new DBConnectionCheckTask(this);
        dbCheckTask.runTaskTimerAsynchronously(this, 20, 20 * 5);

        KDSListeners.init(this);

        KDSCommands.init(this);

        // If player already in server, load data for each player
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                kdDataContainer.loadPlayerData(player);
            });
        }

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        kdDataContainer.saveAllPlayerData(false, true);

        if (sqlHandler != null) {
            sqlHandler.closeConnection();
        }
        if (sql.isConnected()) {
            sql.close();
        }
        if (saveTask != null) saveTask.cancel();
        if (dbCheckTask != null) dbCheckTask.cancel();
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.pluginConfig = new KDStatusConfig(this);
    }

    public PlayerDataMySQLController getKDData() {
        return kdData;
    }

    public KDStatusConfig getPluginConfig() {
        return pluginConfig;
    }

    public KillDeathDataContainer getKdDataContainer() {
        return kdDataContainer;
    }

    public static KDStatusReloaded getPlugin() {
        return plugin;
    }

    public SavePlayerDataTask getSaveTask() {
        return saveTask;
    }

}
