package net.azisaba.kdstatusreloaded.sql;

import net.azisaba.kdstatusreloaded.utils.TimeUnit;
import net.azisaba.kdstatusreloaded.utils.UUIDConverter;
import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataSQLController {

    @Getter(value = AccessLevel.PROTECTED)
    private final SQLHandler handler;

    @Getter(value = AccessLevel.PROTECTED)
    private final String tableName = "killdeathdata";

    public PlayerDataSQLController(SQLHandler handler) {
        this.handler = handler;
    }

    /**
     * テーブルの作成など初期に必要な処理を行います
     *
     * @return 同じインスタンス
     */
    public PlayerDataSQLController init() {
        // initializedされていない場合はする
        if (!handler.isInitialized()) {
            handler.init();
        }

        // kill death のテーブルがなければ作成する
        handler.executeCommand("CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (" +
                "    \"uuid\"  BLOB(16) NOT NULL UNIQUE," +
                "    \"name\"  BLOB(16)," +
                "    \"kills\" INTEGER DEFAULT 0," +
                "    \"deaths\"    INTEGER DEFAULT 0," +
                "    \"daily_kills\"   INTEGER DEFAULT 0," +
                "    \"monthly_kills\" INTEGER DEFAULT 0," +
                "    \"yearly_kills\"  INTEGER DEFAULT 0," +
                "    \"last_updated\"  INTEGER DEFAULT -1," +
                "    PRIMARY KEY(\"uuid\")" +
                ");");

        return this;
    }

    public BigInteger getKills(@NonNull UUID uuid, @NonNull TimeUnit unit) {
        try {
            ResultSet set = handler.executeQuery("select " + unit.getSqlColumnName() + " from \"" + tableName + "\" where uuid='" + UUIDConverter.convert(uuid) + "';");

            if (set.next()) {
                return BigInteger.valueOf(set.getInt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigInteger.valueOf(-1);
    }

    public BigInteger getDeaths(@NonNull UUID uuid) {
        try {
            ResultSet set = handler.executeQuery("select deaths from \"" + tableName + "\" where uuid='" + UUIDConverter.convert(uuid) + "';");

            if (set.next()) {
                return BigInteger.valueOf(set.getInt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigInteger.valueOf(-1);
    }

    public String getName(@NonNull UUID uuid) {
        try {
            ResultSet set = handler.executeQuery("select name from \"" + tableName + "\" where uuid='" + UUIDConverter.convert(uuid) + "';");

            if (set.next()) {
                return set.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getLastUpdated(@NonNull UUID uuid) {
        try {
            ResultSet set = handler.executeQuery("select last_updated from \"" + tableName + "\" where uuid='" + UUIDConverter.convert(uuid) + "';");

            if (set.next()) {
                return set.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public ResultSet getRawData(@NonNull UUID uuid) {
        return handler.executeQuery("select * from \"" + tableName + "\" where uuid='" + UUIDConverter.convert(uuid) + "';");
    }

    public List<KDUserData> getAllData() {

        List<KDUserData> list = new ArrayList<>();

        ResultSet set = handler.executeQuery("SELECT * FROM \"" + tableName + "\";");

        try {
            while (set.next()) {
                UUID uuid = UUID.fromString(UUIDConverter.insertDashUUID(set.getString("uuid")));
                String name = set.getString("name");
                int totalKills = set.getInt("kills");
                int deaths = set.getInt("deaths");
                int dailyKills = set.getInt("daily_kills");
                int monthlyKills = set.getInt("monthly_kills");
                int yearlyKills = set.getInt("yearly_kills");
                long lastUpdated = set.getLong("last_updated");

                list.add(new KDUserData(uuid, name, totalKills, deaths, dailyKills, monthlyKills, yearlyKills, lastUpdated));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;

    }

    public boolean save(@NonNull KDUserData data) {
        String uuid = UUIDConverter.convert(data.getUuid());
        String name = data.getName();
        String totalKills = "" + data.getKills(TimeUnit.LIFETIME);
        String deaths = "" + data.getDeaths();
        String dailyKills = "" + data.getKills(TimeUnit.DAILY);
        String monthlyKills = "" + data.getKills(TimeUnit.MONTHLY);
        String yearlyKills = "" + data.getKills(TimeUnit.YEARLY);
        String lastUpdated = "" + data.getLastUpdated();

        int changed = handler.executeCommand("insert or replace into " + tableName + " (uuid, name, kills, deaths, daily_kills, monthly_kills, yearly_kills, last_updated) "
                + "values ('" + uuid + "', "
                + "'" + name + "', "
                + totalKills + ", "
                + deaths + ", "
                + dailyKills + ", "
                + monthlyKills + ", "
                + yearlyKills + ", "
                + lastUpdated + ");");
        return changed >= 0;
    }

    public boolean save(@NonNull KDUserData... data2) {
        if (data2.length <= 0) {
            return true;
        }

        List<String> values = new ArrayList<>();

        for (KDUserData data : data2) {
            String uuid = UUIDConverter.convert(data.getUuid());
            String name = data.getName();
            String totalKills = "" + data.getKills(TimeUnit.LIFETIME);
            String deaths = "" + data.getDeaths();
            String dailyKills = "" + data.getKills(TimeUnit.DAILY);
            String monthlyKills = "" + data.getKills(TimeUnit.MONTHLY);
            String yearlyKills = "" + data.getKills(TimeUnit.YEARLY);
            String lastUpdated = "" + data.getLastUpdated();

            String value = "('" + uuid + "', "
                    + "'" + name + "', "
                    + totalKills + ", "
                    + deaths + ", "
                    + dailyKills + ", "
                    + monthlyKills + ", "
                    + yearlyKills + ", "
                    + lastUpdated + ")";

            values.add(value);
        }

        String cmd = "insert or replace into " + tableName + " (uuid, name, kills, deaths, daily_kills, monthly_kills, yearly_kills, last_updated) values "
                + String.join(", ", values) + ";";

        int changed = handler.executeCommand(cmd);
        return changed >= 0;
    }
}
