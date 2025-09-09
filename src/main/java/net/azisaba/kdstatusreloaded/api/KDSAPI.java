package net.azisaba.kdstatusreloaded.api;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;

@NullMarked
public class KDSAPI {
    public static KDUserData getPlayerData(UUID uuid) {
        Optional<KDUserData> data = KDStatusReloaded.getPlugin().getPlayerKd().getPlayerDataWithNoCaching(uuid);
        return data.orElseGet(() -> new KDUserData(uuid, ""));
    }
}
