package jp.azisaba.lgw.kdstatus.api;

import jp.azisaba.lgw.kdstatus.sql.KDUserData;
import jp.azisaba.lgw.kdstatus.sql.KillRankingData;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * This interface for modeling KDStatusReloaded API. Make sure this one is not public.
 */
interface KDAPIInterface {
    /**
     * Get specific user's data. If data wasn't found, create new userdata.
     * @param uuid UUID of player
     * @param name Name of player
     * @return returns KDUserData.
     */
    @NotNull
    KDUserData getOrCreateUserData(@NotNull UUID uuid, @NotNull String name);

    /**
     * Get specific user's data
     * @param uuid UUID of player
     * @return returns KDUserData. If failed, returns null.
     */
    @Nullable
    KDUserData getUserData(@NotNull UUID uuid);

    /**
     * Get specific user's ranking
     * @param uuid UUID of player
     * @param unit Unit of ranking
     * @return returns ranking order. If failed, returns -1.
     */
    int getRank(@NotNull UUID uuid, @NotNull TimeUnit unit);

    /**
     * Get ranking by timeunit
     * @param unit TimeUnit of ranking
     * @param maxSize Maximum size of ranking
     * @return List of {@link KillRankingData}. If failed, returns empty list.
     */
    List<KillRankingData> getTopKillRankingData(@NotNull TimeUnit unit, int maxSize);

    // === Won't need to implement each ===
    /**
     * Get specific user's kill count
     * @param uuid UUID of player
     * @param unit Unit of ranking
     * @return returns kill count. If failed, returns -1.
     */
    default int getKills(@NotNull UUID uuid, @NotNull TimeUnit unit) {
        var userdata = getUserData(uuid);
        if(userdata == null) return -1;
        return userdata.getKills(unit);
    }

    /**
     * Get specific user's death count
     * @param uuid UUID of player
     * @return returns total death count. If failed, returns -1
     */
    default int getDeaths(@NotNull UUID uuid) {
        var userdata = getUserData(uuid);
        if(userdata == null) return -1;
        return userdata.getDeaths();
    }
}
