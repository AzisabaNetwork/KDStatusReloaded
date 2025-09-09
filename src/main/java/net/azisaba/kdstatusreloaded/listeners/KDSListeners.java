package net.azisaba.kdstatusreloaded.listeners;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class KDSListeners {
    public static void init(KDStatusReloaded plugin) {
        register(plugin, new JoinQuitListener(plugin.getKdDataContainer()));
        register(plugin, new KillDeathListener(plugin));
    }

    public static void register(JavaPlugin plugin, Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
