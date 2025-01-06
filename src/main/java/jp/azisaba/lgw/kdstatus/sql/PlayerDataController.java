package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public interface PlayerDataController {
    boolean createTable();

    boolean exist(UUID uuid);

    boolean create(KDUserData data);

    boolean update(KDUserData data);

    BigInteger getKills(@NotNull UUID uuid, @NotNull TimeUnit unit);

    BigInteger getDeaths(@NotNull UUID uuid);

    String getName(UUID uuid);

    long getLastUpdated(@NotNull UUID uuid);

    ResultSet getRawData(@NotNull UUID uuid);

    int getRank(UUID uuid, TimeUnit unit);

    List<KillRankingData> getTopKillRankingData(TimeUnit unit, int count);
}
