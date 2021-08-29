package jp.azisaba.lgw.kdstatus;

import java.io.File;
import java.sql.SQLException;

import jp.azisaba.lgw.kdstatus.sql.*;
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

    private SQLHandler sqlHandler = null;

    public MySQLHandler sql;

    private PlayerDataMySQLController kdData;

    public static KDStatusReloaded getPlugin() {
        return plugin;
    }

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

        sql = new MySQLHandler();

        this.kdData = new PlayerDataMySQLController(this);

        try {
            sql.connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getLogger().warning("Failed to connect SQLDatabase.");
        }
        if(sql.isConnected()){
            getLogger().info("Connected SQLDatabase!");

            //ここでテーブル作るぞ
            this.kdData.createTable();

        }

        saveTask = new SavePlayerDataTask(this);
        saveTask.runTaskTimerAsynchronously(this, 20 * 60 * 3, 20 * 60 * 3);

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
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.pluginConfig = new KDStatusConfig(this);
        this.pluginConfig.loadConfig();
    }

    public KDStatusConfig getPluginConfig(){
        return pluginConfig;
    }
    public KillDeathDataContainer getKdDataContainer(){
        return kdDataContainer;
    }
    public SavePlayerDataTask getSaveTask(){
        return saveTask;
    }
    public PlayerDataMySQLController getKDData(){
        return kdData;
    }

}
