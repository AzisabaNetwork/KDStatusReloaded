package net.azisaba.kdstatusreloaded.playerkd;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.api.KillCountType;
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
        kdDatabase = new KDDatabase(plugin.getPluginConfig().database, plugin.getDataFolder());
        kdCache = new KDCache(kdDatabase.kdUserDataRepository());
        plugin.register(new PlayerEventListener(kdCache, plugin.getPluginConfig().world));
    }

    public void onDisable() {
        kdCache.removeAll();
        kdDatabase.shutdown();
    }

    public void loadAll(List<UUID> playerUuids) {
        playerUuids.forEach(kdCache::getData);
    }

    public KDUserData getPlayerData(UUID uuid) {
        return kdCache.getData(uuid);
    }

    public Optional<KDUserData> getPlayerDataWithNoCaching(UUID uuid) {
        if(kdCache.isCached(uuid)) return Optional.of(kdCache.getData(uuid));
        return kdDatabase.kdUserDataRepository().findById(uuid);
    }

    public int getRanking(KillCountType type, UUID uuid) {
        return kdDatabase.kdUserDataRepository().getRanking(type.columnName, type.getFirstMilliSecond(), uuid);
    }

    public List<KDUserData> getTops(KillCountType type, int limit) {
        return kdDatabase.kdUserDataRepository().findTop(type.columnName, limit);
    }

    public void migrate() {
        kdDatabase.migrate();
    }
}
