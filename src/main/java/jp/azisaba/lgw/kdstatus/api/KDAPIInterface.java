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
     * Get specific user's data
     * @param uuid UUID of player
     * @param name Name of player
     * @return returns KDUserData. If failed, returns null.
     */
    @Nullable
    KDUserData getUserData(@NotNull UUID uuid, @NotNull String name);

    /**
     * Get specific user's ranking
     * @param uuid UUID of player
     * @param unit Name of player
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
}
