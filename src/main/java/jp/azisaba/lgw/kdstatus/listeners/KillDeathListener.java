package jp.azisaba.lgw.kdstatus.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import lombok.RequiredArgsConstructor;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;

@RequiredArgsConstructor
public class KillDeathListener implements Listener {

    private final KDStatusReloaded plugin;

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();

        if ( killer == null ) {
            return;
        }

        World world = killer.getWorld();

        if ( plugin.getPluginConfig().disableKillWorldList.contains(killer.getWorld().getName()) ) {
            if ( plugin.getPluginConfig().showLogInConsole ) {
                plugin.getLogger()
                        .info(killer.getName() + "のキル数加算をキャンセル (\"" + world.getName() + "\" が無効なワールドに設定されている)");
            }
            return;
        }

        plugin.getKdDataContainer().getPlayerData(killer, true).addKill(1);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        World world = p.getWorld();

        if ( plugin.getPluginConfig().disableDeathWorldList.contains(world.getName()) ) {
            if ( plugin.getPluginConfig().showLogInConsole ) {
                plugin.getLogger()
                        .info(p.getName() + "のデス数加算をキャンセル (\"" + world.getName() + "\" が無効化されているワールドに設定されている)");
            }
            return;
        }

        plugin.getKdDataContainer().getPlayerData(p, true).addDeath(1);
    }
}
