package jp.azisaba.lgw.kdstatus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;

import jp.azisaba.lgw.kdstatus.commands.KDStatusCommand;
import jp.azisaba.lgw.kdstatus.commands.MyStatusCommand;
import jp.azisaba.lgw.kdstatus.listeners.JoinQuitListener;
import jp.azisaba.lgw.kdstatus.listeners.KillDeathListener;
import jp.azisaba.lgw.kdstatus.utils.Chat;

public class KDStatusReloaded extends JavaPlugin {

    @Getter
    private KDStatusConfig pluginConfig;
    private BukkitTask saveTask;

    @Getter
    private KillDeathDataContainer kdDataContainer;

    @Getter
    private static KDStatusReloaded plugin;

    @Override
    public void onEnable() {

        plugin = this;

        pluginConfig = new KDStatusConfig(this);
        pluginConfig.loadConfig();

        kdDataContainer = new KillDeathDataContainer(this);

        initKDManager();

        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(kdDataContainer), this);
        Bukkit.getPluginManager().registerEvents(new KillDeathListener(this), this);

        Bukkit.getPluginCommand("mystatus").setExecutor(new MyStatusCommand(kdDataContainer));
        Bukkit.getPluginCommand("mystatus").setPermissionMessage(Chat.f("&c権限がありません。運営に報告してください。"));
        Bukkit.getPluginCommand("kdstatus").setExecutor(new KDStatusCommand(this));
        Bukkit.getPluginCommand("kdstatus").setPermissionMessage(Chat.f("&cこのコマンドを実行する権限がありません！"));

        if ( Bukkit.getOnlinePlayers().size() > 0 ) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                kdDataContainer.registerPlayer(player);
            });
        }

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {

        if ( saveTask != null ) {
            saveTask.cancel();
        }

        kdDataContainer.saveAllPlayerData(false, true);
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void reloadPluginConfig() {
        this.pluginConfig = new KDStatusConfig(this);
        this.pluginConfig.loadConfig();
    }

    @SuppressWarnings("deprecation")
    private void initKDManager() {
        KDManager.init(this, kdDataContainer);
    }
}
