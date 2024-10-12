package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class PlayerDataHikariMySQLController implements PlayerDataController {
    private final Logger logger;
    private HikariMySQLDatabase db;

    public void connect() {}

    @Override
    public boolean createTable() {
        return db.executeUpdate("CREATE TABLE IF NOT EXISTS kill_death_data "
                + "(uuid VARCHAR(64) NOT NULL ,name VARCHAR(36) NOT NULL," +
                "kills INT DEFAULT 0, " +
                "deaths INT DEFAULT 0 ," +
                "daily_kills INT DEFAULT 0," +
                "monthly_kills INT DEFAULT 0," +
                "yearly_kills INT DEFAULT 0," +
                "last_updated BIGINT DEFAULT -1 )");
    }

    @Override
    public boolean exist(UUID uuid) {
        try(PreparedStatement pstmt = db.preparedStatement("SELECT * FROM kill_death_data WHERE name=?")) {
            if(pstmt == null) return false;
            pstmt.setString(1, uuid.toString());
            // TODO check this resource leak
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get exists", e);
            return false;
        }
    }

    @Override
    public boolean create(KDUserData data) {
        if(exist(data.getUuid())) {
            logger.warning("This user data was already created!");
            return false;
        }

        try(PreparedStatement ps = db.preparedStatement("INSERT INTO kill_death_data (uuid,name,kills,deaths,daily_kills,monthly_kills,yearly_kills,last_updated) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1,data.getUuid().toString());
            ps.setString(2,data.getName());
            ps.setInt(3,data.getKills(TimeUnit.LIFETIME));
            ps.setInt(4,data.getDeaths());
            ps.setInt(5,data.getKills(TimeUnit.DAILY));
            ps.setInt(6,data.getKills(TimeUnit.MONTHLY));
            ps.setInt(7,data.getKills(TimeUnit.YEARLY));
            ps.setLong(8,data.getLastUpdated());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create userdata", e);
            return false;
        }
    }

    @Override
    public boolean update(KDUserData data) {
        try(PreparedStatement ps = db.preparedStatement("UPDATE kill_death_data SET name=? ,kills=? ,deaths=? ,daily_kills=? ,monthly_kills=? ,yearly_kills=? ,last_updated=? WHERE uuid=?")) {
            ps.setString(8,data.getUuid().toString());
            ps.setString(1,data.getName());
            ps.setInt(2,data.getKills(TimeUnit.LIFETIME));
            ps.setInt(3,data.getDeaths());
            ps.setInt(4,data.getKills(TimeUnit.DAILY));
            ps.setInt(5,data.getKills(TimeUnit.MONTHLY));
            ps.setInt(6,data.getKills(TimeUnit.YEARLY));
            ps.setLong(7,data.getLastUpdated());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update userdata", e);
            return false;
        }
    }

    @Override
    public BigInteger getKills(@NonNull UUID uuid, @NonNull TimeUnit unit) {
        return null;
    }

    @Override
    public BigInteger getDeaths(@NonNull UUID uuid) {
        return null;
    }

    @Override
    public String getName(UUID uuid) {
        return "";
    }

    @Override
    public long getLastUpdated(@NonNull UUID uuid) {
        return 0;
    }

    @Override
    public ResultSet getRawData(@NonNull UUID uuid) {
        return null;
    }

    @Override
    public int getRank(UUID uuid, TimeUnit unit) {
        return 0;
    }

    @Override
    public List<KillRankingData> getTopKillRankingData(TimeUnit unit, int count) {
        return Collections.emptyList();
    }
}
