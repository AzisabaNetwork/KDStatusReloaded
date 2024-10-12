package jp.azisaba.lgw.kdstatus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import jp.azisaba.lgw.kdstatus.sql.KillDeathDataContainer;

public class JoinQuitListener implements Listener {

    private final KillDeathDataContainer dataContainer;

    public JoinQuitListener(KillDeathDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        dataContainer.loadPlayerData(p);
    }
}
