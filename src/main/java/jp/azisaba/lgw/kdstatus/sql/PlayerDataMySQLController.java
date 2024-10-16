package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import lombok.NonNull;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataMySQLController {

    KDStatusReloaded plugin;

    public PlayerDataMySQLController(KDStatusReloaded plugin){ this.plugin = plugin; }

    public void createTable(){

        try{
            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS kill_death_data "
                    + "(uuid VARCHAR(64) NOT NULL ,name VARCHAR(36) NOT NULL," +
                    "kills INT DEFAULT 0, " +
                    "deaths INT DEFAULT 0 ," +
                    "daily_kills INT DEFAULT 0," +
                    "monthly_kills INT DEFAULT 0," +
                    "yearly_kills INT DEFAULT 0," +
                    "last_updated BIGINT DEFAULT -1 )");

            ps.executeUpdate();

        }catch (SQLException e){e.printStackTrace();}

    }

    public boolean exist(UUID uuid){

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM kill_death_data WHERE name=?");
            ps.setString(1,uuid.toString());

            ResultSet result = ps.executeQuery();

            if(result.next()){
                return true;
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public void create(KDUserData data){

        if(exist(data.getUuid()))
            return;

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("INSERT INTO kill_death_data (uuid,name,kills,deaths,daily_kills,monthly_kills,yearly_kills,last_updated) VALUES (?,?,?,?,?,?,?,?)");
            ps.setString(1,data.getUuid().toString());
            ps.setString(2,data.getName());
            ps.setInt(3,data.getKills(TimeUnit.LIFETIME));
            ps.setInt(4,data.getDeaths());
            ps.setInt(5,data.getKills(TimeUnit.DAILY));
            ps.setInt(6,data.getKills(TimeUnit.MONTHLY));
            ps.setInt(7,data.getKills(TimeUnit.YEARLY));
            ps.setLong(8,data.getLastUpdated());

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean update(KDUserData data){

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("UPDATE kill_death_data SET name=? ,kills=? ,deaths=? ,daily_kills=? ,monthly_kills=? ,yearly_kills=? ,last_updated=? WHERE uuid=?");
            ps.setString(8,data.getUuid().toString());
            ps.setString(1,data.getName());
            ps.setInt(2,data.getKills(TimeUnit.LIFETIME));
            ps.setInt(3,data.getDeaths());
            ps.setInt(4,data.getKills(TimeUnit.DAILY));
            ps.setInt(5,data.getKills(TimeUnit.MONTHLY));
            ps.setInt(6,data.getKills(TimeUnit.YEARLY));
            ps.setLong(7,data.getLastUpdated());

            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public BigInteger getKills(@NonNull UUID uuid, @NonNull TimeUnit unit) {

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT "+ unit.getSqlColumnName() + " FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if(result.next()){
                return BigInteger.valueOf(result.getInt(1));
            }

            ps.close();

            return BigInteger.valueOf(-1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigInteger.valueOf(-1);

    }

    public BigInteger getDeaths(@NonNull UUID uuid) {

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT death FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if(result.next()){
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

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT name FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if(result.next()){
                return result.getString(1);
            }

            ps.close();

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public long getLastUpdated(@NonNull UUID uuid) {

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT last_updated FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet result = ps.executeQuery();

            if(result.next()){
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

        PreparedStatement ps;
        try {
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM kill_death_data WHERE uuid=?");
            ps.setString(1, uuid.toString());
            return ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private static final String RANK_QUERY = "SELECT kills, uuid, (SELECT count(*) FROM kill_death_data i2 WHERE i1.%s < i2.%s) + 1 AS 'rank' FROM kill_death_data i1 WHERE uuid=? and last_updated > ?";

    public int getRank(UUID uuid,TimeUnit unit){
        String columnName = unit.getSqlColumnName();
        try(PreparedStatement p = plugin.sql.getConnection().prepareStatement(String.format(RANK_QUERY, columnName, columnName))) {
            p.setString(1, uuid.toString());
            p.setLong(2, TimeUnit.getFirstMilliSecond(unit));
            ResultSet result = p.executeQuery();
            if(result.next()) {
                return result.getInt("rank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;

    }

    public List<KillRankingData> getTopKillRankingData(TimeUnit unit, int count){

        try {

            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("select uuid, name, " + unit.getSqlColumnName()
                    + " from kill_death_data"
                    + " where last_updated >= ?"
                    + " order by " + unit.getSqlColumnName() + " DESC"
                    + " LIMIT ?");

            ps.setLong(1,TimeUnit.getFirstMilliSecond(unit));
            ps.setInt(2,count);

            List<KillRankingData> ranking = new ArrayList<>();

            ResultSet result = ps.executeQuery();

            while (result.next()){

                UUID uuid = UUID.fromString(result.getString("uuid"));
                String mcid = result.getString("name");
                int kills = result.getInt(unit.getSqlColumnName());

                // プレイヤーデータの作成
                KillRankingData data = new KillRankingData(uuid, mcid, kills);

                // リストに追加
                ranking.add(data);

            }

            ps.close();

            return ranking;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

}
