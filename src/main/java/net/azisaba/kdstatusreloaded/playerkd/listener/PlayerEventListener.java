package net.azisaba.kdstatusreloaded.playerkd.listener;

import net.azisaba.kdstatusreloaded.config.KDConfig;
import net.azisaba.kdstatusreloaded.playerkd.cache.KDCache;
import net.azisaba.kdstatusreloaded.utils.Chat;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NullMarked
public class PlayerEventListener implements Listener {
    private static final Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private final KDCache kdCache;
    private final KDConfig.KillDeathConfig killDeathConfig;

    public PlayerEventListener(KDCache kdCache, KDConfig.KillDeathConfig killDeathConfig) {
        this.kdCache = kdCache;
        this.killDeathConfig = killDeathConfig;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        kdCache.store(p.getUniqueId(), p.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        kdCache.remove(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        // increment death count
        World victimWorld = victim.getWorld();
        if (killDeathConfig.disableDeathWorldList.contains(victimWorld.getName())) {
            if (killDeathConfig.showCountCancelled) {
                logger.info(Chat.f("{0}のデス数加算をキャンセル (\"{1}\" が無効化にするワールドに指定されているため)", victim.getName(), victimWorld.getName()));
            }
        } else {
            kdCache.addDeath(victim.getUniqueId(), victim.getName(), 1);
        }

        // increment kill count
        if (killer != null) {
            World killerWorld = killer.getWorld();
            if (killDeathConfig.disableKillWorldList.contains(killerWorld.getName())) {
                if (killDeathConfig.showCountCancelled) {
                    logger.info(Chat.f("{0}のキル数加算をキャンセル (\"{1}\" が無効にするワールドに指定されているため)", killer.getName(), killerWorld.getName()));
                }
            } else {
                kdCache.addKill(killer.getUniqueId(), killer.getName(), 1);
            }
        }
    }
}
