package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import lombok.NonNull;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public interface PlayerDataController {
    boolean createTable();

    boolean exist(UUID uuid);

    boolean create(KDUserData data);

    boolean update(KDUserData data);

    BigInteger getKills(@NonNull UUID uuid, @NonNull TimeUnit unit);

    BigInteger getDeaths(@NonNull UUID uuid);

    String getName(UUID uuid);

    long getLastUpdated(@NonNull UUID uuid);

    ResultSet getRawData(@NonNull UUID uuid);

    int getRank(UUID uuid, TimeUnit unit);

    List<KillRankingData> getTopKillRankingData(TimeUnit unit, int count);
}
