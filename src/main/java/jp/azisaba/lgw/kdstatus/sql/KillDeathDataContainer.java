package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import jp.azisaba.lgw.kdstatus.utils.Chat;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import jp.azisaba.lgw.kdstatus.utils.UUIDConverter;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.util.*;

public class KillDeathDataContainer {

    private final PlayerDataSQLController sqlController;

    private final HashMap<UUID, KDUserData> playerDataCache = new HashMap<>();

    private boolean isMigrated = KDStatusReloaded.getPlugin().getConfig().getBoolean("migrated", false);

    public KillDeathDataContainer(PlayerDataSQLController sqlController) {
        this.sqlController = sqlController;
    }

    /**
     * プレイヤーの戦績を取得します
     *
     * @param p               対象プレイヤー
     * @param loadIfNotLoaded cacheに無かったらファイルから呼び出すかどうか
     *                        (trueで呼び出す、falseでcacheからのみ参照する)
     * @return 対象プレイヤーの戦績 (readFromFileがfalseかつ、cacheにない場合はnull)
     */
    public KDUserData getPlayerData(Player p, boolean loadIfNotLoaded) {

        // cacheに保存されている場合はそこから取得
        if (playerDataCache.containsKey(p.getUniqueId())) {
            return playerDataCache.get(p.getUniqueId());
        }

        // cacheから呼び出さない&cacheにも無かった場合はnullを返す
        if (!loadIfNotLoaded) {
            return null;
        }

        // ファイルから読み込みreturn
        return loadPlayerData(p);
    }

    /**
     * プレイヤーの戦績をファイルから読み込みます。すでにcacheに保存してある場合はそこから取得して返します
     *
     * @param p 対象プレイヤー
     * @return cacheに保存されている戦績か、ファイルから読み込まれたプレイヤーの戦績
     * @throws NullPointerException 対象プレイヤーがnullの場合
     */
    public KDUserData loadPlayerData(@NotNull Player p) {
        if (playerDataCache.containsKey(p.getUniqueId())) {
            return playerDataCache.get(p.getUniqueId());
        }

        File folder = new File(KDStatusReloaded.getPlugin().getDataFolder(), "PlayerData");
        File file = new File(folder, p.getUniqueId() + ".yml");

        KDUserData data = null;

        // ファイルが存在している場合はそこから読み込む。なければSQLiteから読み込む
        if (file.exists()) {
            data = new KDUserData(p);
        } else if (!isMigrated) {
            ResultSet set = sqlController.getRawData(p.getUniqueId());

            try {
                if (set.next()) {
                    int totalKills = set.getInt("kills");
                    int deaths = set.getInt("deaths");
                    int dailyKills = set.getInt("daily_kills");
                    int monthlyKills = set.getInt("monthly_kills");
                    int yearlyKills = set.getInt("yearly_kills");
                    long lastUpdated = set.getLong("last_updated");

                    data = new KDUserData(p.getUniqueId(), p.getName(), totalKills, deaths, dailyKills, monthlyKills, yearlyKills, lastUpdated);
                } else {
                    data = new KDUserData(p.getUniqueId(), p.getName(), 0, 0, 0, 0, 0, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            data = KDStatusReloaded.getPlugin().getKDData().getUserData(p.getUniqueId(), p.getName());
        }

        // データがnullの場合はnullを返す
        if (data == null) {
            return null;
        }

        playerDataCache.put(p.getUniqueId(), data);

        return data;
    }

    public boolean removeUserDataFromCache(UUID uuid) {
        return playerDataCache.remove(uuid) != null;
    }

    /**
     * cacheに保存されている戦績を(ファイルに保存して)cacheから削除します
     *
     * @param p    対象プレイヤー
     * @param save ファイルにセーブするかどうか
     */
    public boolean unloadPlayer(@NotNull Player p, boolean save) {
        if (!playerDataCache.containsKey(p.getUniqueId())) {
            return true;
        }

        if (save) {
            boolean success = savePlayerData(playerDataCache.get(p.getUniqueId()));
            if (!success) {
                return false;
            }
        }

        playerDataCache.remove(p.getUniqueId());
        return true;
    }

    public boolean savePlayerData(@NotNull KDUserData data) {
        if (!isMigrated) {
            return sqlController.save(data);
        } else {
            return KDStatusReloaded.getPlugin().getKDData().update(data);
        }
    }

    public boolean savePlayerData(@NotNull Player p) {
        KDUserData userData = getPlayerData(p, false);
        if (userData == null) return true;
        return savePlayerData(userData);
    }

    /**
     * cache上の全ての戦績を保存します
     *
     * @param async 非同期で実行するかどうか
     * @param clear 保存した後cacheから削除するかどうか
     */
    public void saveAllPlayerData(boolean async, boolean clear) {

        List<KDUserData> data = new ArrayList<>(playerDataCache.values());

        if (data.size() <= 0) {
            return;
        }

        if (async) {
            new Thread() {
                public void run() {
                    saveAllPlayerData(false, clear);
                }
            }.start();
            return;
        }

        boolean success;

        if (!isMigrated) {
            success = sqlController.save(data.toArray(new KDUserData[data.size()]));
        } else {
            data.forEach(d -> KDStatusReloaded.getPlugin().getKDData().update(d));
            success = true;
        }

        if (success && clear) {
            playerDataCache.clear();
        } else if (success) {
            for (UUID uuid : new ArrayList<UUID>(playerDataCache.keySet())) {
                if (Bukkit.getPlayer(uuid) == null) {
                    playerDataCache.remove(uuid);
                }
            }
        }
    }

    /**
     * 指定した {@link TimeUnit} の、指定した数のランキングを取得します
     *
     * @param count 取得したいランキング数
     * @return 取得されたランキングのリスト / 失敗したらnull
     * @throws IllegalStateException SQLHandlerが初期化されていなかった場合
     */
    public List<KillRankingData> getTopKillRankingData(TimeUnit unit, int count) throws IllegalStateException {

        if (!isMigrated) {

            // SQLHandlerが初期化されていない場合
            if (!sqlController.getHandler().isInitialized()) {
                throw new IllegalStateException("SQLHandler is not initialized yet.");
            }

            // データを取得する
            try {
                ResultSet set = sqlController.getHandler().executeQuery("select uuid, name, " + unit.getSqlColumnName()
                        + " from " + sqlController.getTableName()
                        + " where last_updated >= " + getFirstMilliSecond(unit)
                        + " order by " + unit.getSqlColumnName() + " DESC"
                        + " LIMIT " + count);

                // リスト作成
                List<KillRankingData> ranking = new ArrayList<>();

                // 行がある限り追加していく
                while (set.next()) {
                    UUID uuid = UUID.fromString(UUIDConverter.insertDashUUID(set.getString("uuid")));
                    String mcid = set.getString("name");
                    int kills = set.getInt(unit.getSqlColumnName());

                    // プレイヤーデータの作成
                    KillRankingData data = new KillRankingData(uuid, mcid, kills);

                    // リストに追加
                    ranking.add(data);
                }

                // すべて追加し終わったら返す
                return ranking;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            return KDStatusReloaded.getPlugin().getKDData().getTopKillRankingData(unit, count);

        }

        // 失敗したらnull
        return null;
    }

    public int getRanking(@NotNull UUID uuid, @NotNull TimeUnit unit) {

        if (!isMigrated) {
            ResultSet set = sqlController.getHandler().executeQuery(
                    "select uuid, name, kills, (SELECT count(*) FROM " + sqlController.getTableName()
                            + " as p1 WHERE p1." + unit.getSqlColumnName() + " > p." + unit.getSqlColumnName()
                            + ") + 1 as rank FROM " + sqlController.getTableName() + " as p"
                            + " where uuid = '" + UUIDConverter.convert(uuid) + "' and last_updated > " + getFirstMilliSecond(unit) + " order by rank;");

            try {
                if (set.next()) {
                    return set.getInt("rank");
                } else {
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        } else {

            return KDStatusReloaded.getPlugin().getKDData().getRank(uuid, unit);

        }

    }

    private long getFirstMilliSecond(TimeUnit unit) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        if (unit == TimeUnit.DAILY)
            return cal.getTimeInMillis();

        cal.set(Calendar.DATE, 1);

        if (unit == TimeUnit.MONTHLY)
            return cal.getTimeInMillis();

        cal.set(Calendar.MONTH, 0);

        if (unit == TimeUnit.YEARLY)
            return cal.getTimeInMillis();

        return -1;
    }

    public void miguration(Player p) {
        new Thread() {

            private final File folder = new File(KDStatusReloaded.getPlugin().getDataFolder(), "PlayerData");
            private int finished = 0;
            private int fileCount = -1;

            public void run() {
                fileCount = folder.listFiles().length;

                for (File file : Arrays.asList(folder.listFiles())) {
                    UUID uuid = UUID.fromString(file.getName().substring(0, file.getName().lastIndexOf(".")));

                    if (!playerDataCache.containsKey(uuid)) {
                        // ロードしてSQLにセーブする
                        KDUserData data = new KDUserData(uuid);
//                        data.fixCorrectValue();
                        boolean success = sqlController.save(data);

                        if (!success)
                            break;
                    }

                    file.delete();

                    finished++;
                    JSONMessage.create(Chat.f("&e移行中... &d{0}/{1}", finished, fileCount)).actionbar(p);
                }

                JSONMessage.create(Chat.f("&a完了！")).actionbar(p);
            }
        }.start();
    }

    public void migrationToMySQL(Player p) {

        new Thread() {

            private int finished = 0;

            public void run() {

                sqlController.getAllData().forEach(data -> {

                    KDStatusReloaded.getPlugin().getKDData().create(data);

                    finished++;
                    p.sendMessage(Chat.f("&e移行中... &d{0}個完了", finished));
                });

                KDStatusReloaded.getPlugin().getConfig().set("migrated", true);
                KDStatusReloaded.getPlugin().saveConfig();
                isMigrated = true;
                p.sendMessage(Chat.f("&a完了！"));


            }

        }.start();

    }
}
