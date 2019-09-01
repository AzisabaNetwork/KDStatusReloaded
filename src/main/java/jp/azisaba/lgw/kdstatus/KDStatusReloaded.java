package jp.azisaba.lgw.kdstatus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;

import lombok.Getter;

import jp.azisaba.lgw.kdstatus.commands.MyStatusCommand;
import jp.azisaba.lgw.kdstatus.listeners.JoinQuitListener;
import jp.azisaba.lgw.kdstatus.listeners.KillDeathListener;

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
        Bukkit.getPluginCommand("mystatus").setPermissionMessage(ChatColor.RED + "権限がありません。運営に報告してください。");

        // 権限がありません。運営に報告してください。

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

    @SuppressWarnings("deprecation")
    private void initKDManager() {
        KDManager.init(this, kdDataContainer);
    }
}
