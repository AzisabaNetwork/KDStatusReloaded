package jp.azisaba.lgw.kdstatus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.RequiredArgsConstructor;

import jp.azisaba.lgw.kdstatus.KillDeathDataContainer;

@RequiredArgsConstructor
public class JoinQuitListener implements Listener {

    private final KillDeathDataContainer dataContainer;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        dataContainer.registerPlayer(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        dataContainer.unRegisterPlayer(p, true);
    }
}
