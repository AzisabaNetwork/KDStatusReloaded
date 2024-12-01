package jp.azisaba.lgw.kdstatus.listeners;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import jp.azisaba.lgw.kdstatus.utils.Chat;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillDeathListener implements Listener {

    private final KDStatusReloaded plugin;

    public KillDeathListener(KDStatusReloaded plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        World world = killer.getWorld();

        if (plugin.getPluginConfig().disableKillWorldList.contains(killer.getWorld().getName())) {
            if (plugin.getPluginConfig().showLogInConsole) {
                plugin.getLogger().info(Chat.f("{0}のキル数加算をキャンセル (\"{1}\" が無効にするワールドに指定されているため)", killer.getName(), world.getName()));
            }
            return;
        }

        plugin.getKdDataContainer().getPlayerData(killer, true).addKill(1);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        World world = p.getWorld();

        if (plugin.getPluginConfig().disableDeathWorldList.contains(world.getName())) {
            if (plugin.getPluginConfig().showLogInConsole) {
                plugin.getLogger().info(Chat.f("{0}のデス数加算をキャンセル (\"{1}\" が無効化にするワールドに指定されているため)", p.getName(), world.getName()));
            }
            return;
        }

        plugin.getKdDataContainer().getPlayerData(p, true).addDeath(1);
    }
}
