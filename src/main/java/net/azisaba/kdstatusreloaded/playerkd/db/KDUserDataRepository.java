package net.azisaba.kdstatusreloaded.playerkd.db;

import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RegisterConstructorMapper(KDUserData.class)
public interface KDUserDataRepository {
    @SqlQuery("SELECT * FROM kill_death_data WHERE uuid = :uuid")
    Optional<KDUserData> findById(@Bind("uuid") UUID uuid);

    @SqlQuery("SELECT * FROM kill_death_data ORDER BY kills DESC LIMIT :limit")
    List<KDUserData> findTopByTotalKills(@Bind("limit") int limit);

    @SqlUpdate("INSERT INTO kill_death_data (uuid, name, kills, deaths, daily_kills, monthly_kills, yearly_kills, last_updated) VALUES (:uuid, :name, :totalKills, :deaths, :dailyKills, :monthlyKills, :yearlyKills, :lastUpdated)")
    void upsert(@BindMethods KDUserData data);
}
