package net.azisaba.kdstatusreloaded.api;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.playerkd.PlayerKD;
import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class KDSAPI {
    private static PlayerKD playerKd() {
        return KDStatusReloaded.getPlugin().getPlayerKd();
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
