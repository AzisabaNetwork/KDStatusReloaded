package net.azisaba.kdstatusreloaded.playerkd;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import net.azisaba.kdstatusreloaded.playerkd.cache.KDCache;
import net.azisaba.kdstatusreloaded.playerkd.db.KDDatabase;
import net.azisaba.kdstatusreloaded.playerkd.listener.PlayerEventListener;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class PlayerKD {
    private final KDDatabase kdDatabase;
    private final KDCache kdCache;

    public PlayerKD(KDStatusReloaded plugin) {
        kdDatabase = new KDDatabase(plugin.getPluginConfig().database);
        kdCache = new KDCache(kdDatabase.kdUserDataRepository());
        plugin.register(new PlayerEventListener(kdCache, plugin.getPluginConfig().world));
    }

    public void onDisable() {
        kdCache.removeAll();
        kdDatabase.shutdown();
    }

    public void loadAll(List<UUID> playerUuids) {
        playerUuids.forEach(kdCache::get);
    }

    public KDUserData getPlayerData(UUID uuid) {
        return kdCache.get(uuid);
    }

    public Optional<KDUserData> getPlayerDataWithNoCaching(UUID uuid) {
        if(kdCache.isCached(uuid)) return Optional.of(kdCache.get(uuid));
        return kdDatabase.kdUserDataRepository().findById(uuid);
    }

    public void migrate() {
        kdDatabase.migration();
    }
}
