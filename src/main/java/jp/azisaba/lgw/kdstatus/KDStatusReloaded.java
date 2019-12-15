package jp.azisaba.lgw.kdstatus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

import jp.azisaba.lgw.kdstatus.commands.KDStatusCommand;
import jp.azisaba.lgw.kdstatus.commands.MyStatusCommand;
import jp.azisaba.lgw.kdstatus.listeners.JoinQuitListener;
import jp.azisaba.lgw.kdstatus.listeners.KillDeathListener;
import jp.azisaba.lgw.kdstatus.sql.KillDeathDataContainer;
import jp.azisaba.lgw.kdstatus.sql.PlayerDataSQLController;
import jp.azisaba.lgw.kdstatus.sql.SQLHandler;
import jp.azisaba.lgw.kdstatus.utils.Chat;

public class KDStatusReloaded extends JavaPlugin {

    @Getter
    private KDStatusConfig pluginConfig;

    @Getter
    private KillDeathDataContainer kdDataContainer;

    @Getter
    private static KDStatusReloaded plugin;

    private SQLHandler sqlHandler = null;

    @Override
    public void onEnable() {

        plugin = this;

        pluginConfig = new KDStatusConfig(this);
        pluginConfig.loadConfig();

        sqlHandler = new SQLHandler(new File(getDataFolder(), "playerData.db"));
        kdDataContainer = new KillDeathDataContainer(new PlayerDataSQLController(sqlHandler).init());

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

        if (sqlHandler != null) {
            sqlHandler.closeConnection();
        }
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.pluginConfig = new KDStatusConfig(this);
        this.pluginConfig.loadConfig();
    }
}
