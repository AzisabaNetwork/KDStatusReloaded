package net.azisaba.kdstatusreloaded.playerkd.cache;

import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import net.azisaba.kdstatusreloaded.playerkd.db.KDUserDataRepository;
import org.jspecify.annotations.NullMarked;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

@NullMarked
public class KDCache {
    private final HashMap<UUID, KDUserData> cacheMap = new HashMap<>();

    private final KDUserDataRepository dataRepository;

    public KDCache(KDUserDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public KDUserData get(UUID uuid) {
        if (!cacheMap.containsKey(uuid)) {
            cacheMap.put(
                    uuid,
                    dataRepository.findById(uuid).orElse(new KDUserData(uuid, ""))
            );
        }
        return cacheMap.get(uuid).clone();
    }

    public void store(UUID uuid) {
        cacheMap.put(
                uuid,
                dataRepository.findById(uuid).orElse(new KDUserData(uuid, ""))
        );
    }

    public void remove(UUID uuid) {
        KDUserData kdUserData = cacheMap.remove(uuid);
        if (kdUserData != null) {
            // If data cached, upsert to db.
            dataRepository.upsert(kdUserData);
        }
    }

    public boolean isCached(UUID uuid) {
        return cacheMap.containsKey(uuid);
    }

    public void removeAll() {
        cacheMap.forEach((uuid, kdUserData) -> dataRepository.upsert(kdUserData));
    }

    // 責務を超えてしまうが、ここにkill/deathのincrement処理を追記する。
    public void addKill(UUID uuid, int count) {
        KDUserData kdUserData = get(uuid);
        kdUserData.totalKills += count;

        fixCorrectValue(kdUserData);
        kdUserData.dailyKills += count;
        kdUserData.monthlyKills += count;
        kdUserData.yearlyKills += count;

        updateTimestamp(kdUserData);
    }

    public void addDeath(UUID uuid, int count) {
        KDUserData kdUserData = get(uuid);
        kdUserData.deaths += count;

        updateTimestamp(kdUserData);
    }

    protected void fixCorrectValue(KDUserData kdUserData) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(kdUserData.lastUpdated);

        if (now.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
            kdUserData.dailyKills = 0;
            kdUserData.monthlyKills = 0;
            kdUserData.yearlyKills = 0;
        } else if (now.get(Calendar.MONTH) != cal.get(Calendar.MONTH)) {
            kdUserData.dailyKills = 0;
            kdUserData.monthlyKills = 0;
        } else if (now.get(Calendar.DATE) != cal.get(Calendar.DATE)) {
            kdUserData.dailyKills = 0;
        }
    }

    protected void updateTimestamp(KDUserData kdUserData) {
        kdUserData.lastUpdated = System.currentTimeMillis();
    }
}
