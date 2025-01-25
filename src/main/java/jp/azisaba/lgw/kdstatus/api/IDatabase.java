package jp.azisaba.lgw.kdstatus.api;

import jp.azisaba.lgw.kdstatus.sql.KDUserData;
import jp.azisaba.lgw.kdstatus.sql.KillRankingData;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * The interface of database.
 */
public interface IDatabase {
    /**
     * Init database connection
     *
     * @return is succeeded
     */
    boolean init();

    /**
     * Close database connection.
     *
     * @return is succeeded
     */
    boolean close();

    /**
     * Is alive database connection.
     *
     * @return is succeeded
     */
    boolean isAlive();

    /**
     * Is database have userdata which contains this uuid.
     *
     * @param uuid uuid of player
     * @return If it has, returns true. Else returns false.
     */
    boolean exist(UUID uuid);

    /**
     * Set the new userdata.
     *
     * @param uuid uuid of player
     * @param userData the new userdata.
     * @return is succeeded
     */
    boolean set(UUID uuid, @NotNull KDUserData userData);

    /**
     * Get the specific userdata.
     *
     * @param uuid uuid of player
     * @return returns userdata. If failed, returns null.
     */
    @Nullable
    KDUserData getUserData(UUID uuid);

    /**
     * @param uuid uuid of player
     * @param unit ranking timeunit
     * @return ranking order. If failed, returns -1.
     */
    int getRank(UUID uuid, @NotNull TimeUnit unit);

    /**
     * @param unit ranking timeunit
     * @param maxCount maximum list size
     * @return List of RankingData. If failed, returns empty list.
     */
    List<KillRankingData> getTopKillRankingData(TimeUnit unit, int maxCount);
}
