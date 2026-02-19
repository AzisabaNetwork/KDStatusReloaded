package net.azisaba.kdstatusreloaded.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.playerkd.PlayerKD;
import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class KDSAPI {
    private static PlayerKD playerKd() {
        return JavaPlugin.getPlugin(KDStatusReloaded.class).getPlayerKd();
    }

    public static KDUserData getPlayerData(UUID uuid) {
        Optional<KDUserData> data = playerKd().getPlayerDataWithNoCaching(uuid);
        return data.orElseGet(() -> new KDUserData(uuid, ""));
    }

    public static int getPlayerRanking(KillCountType type, UUID uuid) {
        return playerKd().getRanking(type, uuid);
    }

    public static List<KDUserData> getTops(KillCountType type, int limit) {
        return playerKd().getTops(type, limit);
    }
}
