package net.azisaba.kdstatusreloaded.playerkd.db;

import net.azisaba.kdstatusreloaded.playerkd.model.KDUserData;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RegisterConstructorMapper(KDUserData.class)
public interface KDUserDataRepository {
    @SqlQuery("SELECT * FROM kill_death_data WHERE uuid = :uuid")
    Optional<KDUserData> findById(@Bind("uuid") UUID uuid);

    @SqlQuery("SELECT * FROM kill_death_data WHERE last_updated > :unitFirstMilli ORDER BY <column> DESC LIMIT :limit")
    List<KDUserData> findTop(@Define("column") String columnName, @Bind("unitFirstMilli") long unitFirstMilliSecond, @Bind("limit") int limit);

    @SqlQuery("""
            SELECT s.rank
            FROM (
              SELECT uuid,
                     RANK() OVER (ORDER BY <column> DESC) AS rank
              FROM kill_death_data
              WHERE last_updated > :unitFirstMilli
                AND <column> >= 1
            ) s
            WHERE s.uuid = :uuid
            """)
    Optional<Integer> getRanking(@Define("column") String columnName, @Bind("unitFirstMilli") long unitFirstMilliSecond, @Bind("uuid") UUID uuid);

    @SqlUpdate("INSERT INTO kill_death_data (uuid, name, kills, deaths, daily_kills, monthly_kills, yearly_kills, last_updated) " +
            "VALUES (:uuid, :name, :totalKills, :deaths, :dailyKills, :monthlyKills, :yearlyKills, :lastUpdated) " +
            "ON DUPLICATE KEY UPDATE " +
            "name=VALUES(name), kills=VALUES(kills), deaths=VALUES(deaths), " +
            "daily_kills=VALUES(daily_kills), monthly_kills=VALUES(monthly_kills), " +
            "yearly_kills=VALUES(yearly_kills), last_updated=VALUES(last_updated)")
    void upsert(@BindFields KDUserData data);
}
