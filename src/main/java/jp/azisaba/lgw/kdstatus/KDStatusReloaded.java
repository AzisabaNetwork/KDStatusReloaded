package jp.azisaba.lgw.kdstatus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import jp.azisaba.lgw.kdstatus.commands.MyStatusCommand;
import jp.azisaba.lgw.kdstatus.listeners.JoinQuitListener;
import jp.azisaba.lgw.kdstatus.listeners.KillDeathListener;

public class KDStatusReloaded extends JavaPlugin {

    private final String PLUGIN_NAME = "KDStatus";

    public KDStatusConfig config;
    private BukkitTask saveTask;

    @Override
    public void onEnable() {

        config = new KDStatusConfig(this);
        config.loadConfig();

        KDManager.init(this);

        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new KillDeathListener(this), this);

        Bukkit.getPluginCommand("mystatus").setExecutor(new MyStatusCommand());

        if ( Bukkit.getOnlinePlayers().size() > 0 ) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                KDManager.registerPlayer(player);
            });
        }

        Bukkit.getLogger().info(PLUGIN_NAME + " enabled.");
    }

    @Override
    public void onDisable() {

        if ( saveTask != null ) {
            saveTask.cancel();
        }

        KDManager.saveAllPlayerData(false, true);
        Bukkit.getLogger().info(PLUGIN_NAME + " disabled.");
    }
}
