package jp.azisaba.lgw.kdstatus;

import java.io.File;

import jp.azisaba.lgw.kdstatus.sql.*;
import jp.azisaba.lgw.kdstatus.task.DBConnectionCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

import jp.azisaba.lgw.kdstatus.commands.KDStatusCommand;
import jp.azisaba.lgw.kdstatus.commands.MyStatusCommand;
import jp.azisaba.lgw.kdstatus.listeners.JoinQuitListener;
import jp.azisaba.lgw.kdstatus.listeners.KillDeathListener;
import jp.azisaba.lgw.kdstatus.task.SavePlayerDataTask;
import jp.azisaba.lgw.kdstatus.utils.Chat;

public class KDStatusReloaded extends JavaPlugin {

    @Getter
    private KDStatusConfig pluginConfig;
    @Getter
    private KillDeathDataContainer kdDataContainer;
    @Getter
    private static KDStatusReloaded plugin;
    @Getter
    private SavePlayerDataTask saveTask;
    private DBConnectionCheckTask dbCheckTask;

    private SQLHandler sqlHandler = null;

    public HikariMySQLDatabase sql;

    private PlayerDataMySQLController kdData;

    @Override
    public void onEnable() {

        plugin = this;

        getConfig().addDefault("migrated",false);
        getConfig().addDefault("host","localhost");
        getConfig().addDefault("port",3306);
        getConfig().addDefault("database","kdstatusreloaded");
        getConfig().addDefault("username","root");
        getConfig().addDefault("password","password");
        getConfig().options().copyDefaults(true);
        saveConfig();

        pluginConfig = new KDStatusConfig(this);
        pluginConfig.loadConfig();

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

        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(kdDataContainer), this);
        Bukkit.getPluginManager().registerEvents(new KillDeathListener(this), this);

        Bukkit.getPluginCommand("mystatus").setExecutor(new MyStatusCommand(kdDataContainer));
        Bukkit.getPluginCommand("mystatus").setPermissionMessage(Chat.f("&c権限がありません。運営に報告してください。"));
        Bukkit.getPluginCommand("kdstatus").setExecutor(new KDStatusCommand(this));
        Bukkit.getPluginCommand("kdstatus").setPermissionMessage(Chat.f("&cこのコマンドを実行する権限がありません！"));

        if ( Bukkit.getOnlinePlayers().size() > 0 ) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                kdDataContainer.loadPlayerData(player);
            });
        }

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        kdDataContainer.saveAllPlayerData(false, true);

        if ( sqlHandler != null ) {
            sqlHandler.closeConnection();
        }
        if(sql.isConnected()){
            sql.close();
        }
        if(saveTask!=null) saveTask.cancel();
        if(dbCheckTask!=null) dbCheckTask.cancel();
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.pluginConfig = new KDStatusConfig(this);
        this.pluginConfig.loadConfig();
    }

    public PlayerDataMySQLController getKDData(){
        return kdData;
    }

}
