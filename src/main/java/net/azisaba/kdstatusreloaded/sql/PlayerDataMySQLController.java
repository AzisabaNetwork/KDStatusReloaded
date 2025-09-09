package net.azisaba.kdstatusreloaded.sql;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import net.azisaba.kdstatusreloaded.utils.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDataMySQLController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerDataMySQLController.class);
    private final HikariMySQLDatabase sql;
    private final Logger logger;

    public PlayerDataMySQLController(HikariMySQLDatabase sql, Logger logger) {
        this.sql = sql;
        this.logger = logger;
    }

    public void init() {
        if (sql.isConnected()) {
            logger.info("SQL Testing...");
            try (Connection conn = sql.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT 1")) {
                if (pstmt.executeQuery().next()) {
                    logger.info("SQL Test was success!");
                } else {
                    logger.warning("Failed to test SQL Connection");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error on SQL Testing", e);
            }
            logger.info("SQL Test is finished!");

            logger.info("Connected SQLDatabase!");

            //ここでテーブル作るぞ
            createTable();

            logger.info("Table was created!");

        }
    }

    public void createTable() {

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS kill_death_data "
                     + "(uuid VARCHAR(64) NOT NULL ,name VARCHAR(36) NOT NULL," +
                     "kills INT DEFAULT 0, " +
                     "deaths INT DEFAULT 0 ," +
                     "daily_kills INT DEFAULT 0," +
                     "monthly_kills INT DEFAULT 0," +
                     "yearly_kills INT DEFAULT 0," +
                     "last_updated BIGINT DEFAULT -1 )")) {
            logger.info("Creating database table...");

            ps.executeUpdate();
            ps.close();
            logger.info("Successfully to create database table!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean exist(UUID uuid) {

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM kill_death_data WHERE name=?")) {
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            boolean isExist = result.next();
            result.close();

            return isExist;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public void create(KDUserData data) {

        if (exist(data.getUuid()))
            return;

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO kill_death_data (uuid,name,kills,deaths,daily_kills,monthly_kills,yearly_kills,last_updated) VALUES (?,?,?,?,?,?,?,?)");) {
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setInt(3, data.getKills(TimeUnit.LIFETIME));
            ps.setInt(4, data.getDeaths());
            ps.setInt(5, data.getKills(TimeUnit.DAILY));
            ps.setInt(6, data.getKills(TimeUnit.MONTHLY));
            ps.setInt(7, data.getKills(TimeUnit.YEARLY));
            ps.setLong(8, data.getLastUpdated());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean update(KDUserData data) {

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE kill_death_data SET name=? ,kills=? ,deaths=? ,daily_kills=? ,monthly_kills=? ,yearly_kills=? ,last_updated=? WHERE uuid=?")) {
            ps.setString(8, data.getUuid().toString());
            ps.setString(1, data.getName());
            ps.setInt(2, data.getKills(TimeUnit.LIFETIME));
            ps.setInt(3, data.getDeaths());
            ps.setInt(4, data.getKills(TimeUnit.DAILY));
            ps.setInt(5, data.getKills(TimeUnit.MONTHLY));
            ps.setInt(6, data.getKills(TimeUnit.YEARLY));
            ps.setLong(7, data.getLastUpdated());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public BigInteger getKills(@NonNull UUID uuid, @NonNull TimeUnit unit) {

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT " + unit.getSqlColumnName() + " FROM kill_death_data WHERE uuid=?")) {
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                return BigInteger.valueOf(result.getInt(1));
            }

            ps.close();
            result.close();

            return BigInteger.valueOf(-1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigInteger.valueOf(-1);

    }

    public BigInteger getDeaths(@NonNull UUID uuid) {

        try {

            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT death FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                return BigInteger.valueOf(result.getInt(1));
            }

            ps.close();

            return BigInteger.valueOf(-1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigInteger.valueOf(-1);

    }

    public String getName(@NonNull UUID uuid) {

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM kill_death_data WHERE uuid=?")) {
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                return result.getString(1);
            }

            ps.close();
            result.close();

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public long getLastUpdated(@NonNull UUID uuid) {

        try {

            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT last_updated FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if (result.next()) {
                return result.getLong(1);
            }

            ps.close();

            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;

    }

    public ResultSet getRawData(@NonNull UUID uuid) {
        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM kill_death_data WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            return ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * @param uuid UUID of target player
     * @param name Name of target player
     * @return returns userdata. If failed, returns null.
     */
    public KDUserData getUserData(@NonNull UUID uuid, @NonNull String name) {
        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM kill_death_data WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int totalKills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                int dailyKills = rs.getInt("daily_kills");
                int monthlyKills = rs.getInt("monthly_kills");
                int yearlyKills = rs.getInt("yearly_kills");
                long lastUpdated = rs.getLong("last_updated");
                rs.close();

                return new KDUserData(uuid, name, totalKills, deaths, dailyKills, monthlyKills, yearlyKills, lastUpdated);
            } else {
                rs.close();
                KDUserData data = new KDUserData(uuid, name, 0, 0, 0, 0, 0, -1);
                KDStatusReloaded.getPlugin().getKDData().create(data);
                return data;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static final String RANK_QUERY = "SELECT * FROM (SELECT uuid, ${COLUMN_NAME}, last_updated, RANK() over (ORDER BY ${COLUMN_NAME} DESC) as 'rank' FROM kill_death_data WHERE last_updated > ?) s WHERE s.uuid=?";

    public int getRank(UUID uuid, TimeUnit unit) {
        try (Connection conn = sql.getConnection();
             PreparedStatement p = conn.prepareStatement(RANK_QUERY.replace("${COLUMN_NAME}", unit.getSqlColumnName()))) {
            p.setLong(1, TimeUnit.getFirstMilliSecond(unit));
            p.setString(2, uuid.toString());
            if(KDStatusReloaded.getPlugin().getPluginConfig().showLogInConsole) {
                logger.info("Executed query: " + p);
            }
            ResultSet result = p.executeQuery();
            if (result.next()) {
                return result.getInt("rank");
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;

    }

    public List<KillRankingData> getTopKillRankingData(TimeUnit unit, int count) {

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement("select uuid, name, " + unit.getSqlColumnName()
                     + " from kill_death_data"
                     + " where last_updated >= ?"
                     + " order by " + unit.getSqlColumnName() + " DESC"
                     + " LIMIT ?")) {

            ps.setLong(1, TimeUnit.getFirstMilliSecond(unit));
            ps.setInt(2, count);

            List<KillRankingData> ranking = new ArrayList<>();

            ResultSet result = ps.executeQuery();

            while (result.next()) {

                UUID uuid = UUID.fromString(result.getString("uuid"));
                String mcid = result.getString("name");
                int kills = result.getInt(unit.getSqlColumnName());

                // プレイヤーデータの作成
                KillRankingData data = new KillRankingData(uuid, mcid, kills);

                // リストに追加
                ranking.add(data);

            }

            return ranking;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

}
