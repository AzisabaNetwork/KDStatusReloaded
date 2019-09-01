package jp.azisaba.lgw.kdstatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KillDeathDataContainer {

    private final KDStatusReloaded plugin;

    private HashMap<Player, KDUserData> playerDataCache = new HashMap<>();

    /**
     * プレイヤーの戦績を取得します
     *
     * @param p            対象プレイヤー
     * @param readFromFile cacheに無かったらファイルから呼び出すかどうか (trueで呼び出す、falseでcacheからのみ参照する)
     * @return 対象プレイヤーの戦績 (readFromFileがfalseかつ、cacheにない場合はnull)
     */
    public KDUserData getPlayerData(Player p, boolean readFromFile) {

        // cacheに保存されている場合はそこから取得
        if ( playerDataCache.containsKey(p) ) {
            return playerDataCache.get(p);
        }

        // cacheから呼び出さない&cacheにも無かった場合はnullを返す
        if ( !readFromFile ) {
            return null;
        }

        // ファイルから読み込みreturn
        return registerPlayer(p);
    }

    /**
     * プレイヤーの戦績をファイルから読み込みます。すでにcacheに保存してある場合はそこから取得して返します
     *
     * @param p 対象プレイヤー
     * @return cacheに保存されている戦績か、ファイルから読み込まれたプレイヤーの戦績
     *
     * @exception NullPointerException 対象プレイヤーがnullの場合
     */
    public KDUserData registerPlayer(@NonNull Player p) {
        if ( playerDataCache.containsKey(p) ) {
            return playerDataCache.get(p);
        }

        KDUserData data = new KDUserData(p);
        playerDataCache.put(p, data);

        return data;
    }

    /**
     * cacheに保存されている戦績を(ファイルに保存して)cacheから削除します
     *
     * @param p    対象プレイヤー
     * @param save ファイルにセーブするかどうか
     */
    public void unRegisterPlayer(@NonNull Player p, boolean save) {
        if ( !playerDataCache.containsKey(p) ) {
            return;
        }

        if ( save ) {
            playerDataCache.get(p).saveData(true);
        }

        playerDataCache.remove(p);
    }

    /**
     * cache上の全ての戦績を保存します
     *
     * @param async 非同期で実行するかどうか
     * @param clear 保存した後cacheから削除するかどうか
     */
    public void saveAllPlayerData(boolean async, boolean clear) {
        playerDataCache.keySet().forEach(key -> {
            playerDataCache.get(key).saveData(async);
        });

        if ( clear ) {
            playerDataCache.clear();
        }
    }

    /**
     * プレイヤーのキル数を取得します。<br>
     * cacheに保存されているデータがある場合はそこから取得し、cacheに保存されていない場合はファイルから読み込みます
     *
     * @param p 対象プレイヤー
     * @return 取得したキル数
     */
    public int getKills(Player p) {
        return registerPlayer(p).getKills();
    }

    /**
     * プレイヤーのデス数を取得します。<br>
     * cacheに保存されているデータがある場合はそこから取得し、cacheに保存されていない場合はファイルから読み込みます
     *
     * @param p 対象プレイヤー
     * @return 取得したデス数
     */
    public int getDeaths(Player p) {
        return registerPlayer(p).getDeaths();
    }

    /**
     * Dailyキルを取得します
     *
     * @deprecated {@link #getAllKills(TimeUnit)} を使用してください
     * @return 全Dailyキル (0は除く) 1件もない場合は空のMapが返される
     */
    public HashMap<PlayerInfo, Integer> getAllDailyKills() {
        return getAllKills(TimeUnit.DAILY);
    }

    /**
     * Monthlyキルを取得します
     *
     * @deprecated {@link #getAllKills(TimeUnit)} を使用してください
     * @return 全Monthlyキル (0は除く) 1件もない場合は空のMapが返される
     */
    public HashMap<PlayerInfo, Integer> getAllMonthlyKills() {
        return getAllKills(TimeUnit.MONTHLY);
    }

    /**
     * 全てのプレイヤーのTotalキルを取得します
     *
     * @return 全Totalキル (0は除く) 1件もない場合は空のMapが返される
     */
    public HashMap<PlayerInfo, Integer> getAllTotalKills() {

        HashMap<PlayerInfo, Integer> map = new HashMap<>();
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        if ( !folder.exists() ) {
            return map;
        }

        List<String> finished = new ArrayList<>();
        for ( KDUserData data : playerDataCache.values() ) {
            int totalKill = data.getKills();
            if ( totalKill <= 0 ) {
                continue;
            }
            map.put(new PlayerInfo(data.getPlayer().getUniqueId(), data.getPlayer().getName()), totalKill);
            finished.add(data.getPlayer().getName());
        }

        for ( File file : folder.listFiles() ) {
            if ( !file.isFile() || !file.getName().endsWith(".yml") ) {
                continue;
            }
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

            String pName = conf.getString(KDUserData.LAST_NAME_KEY);
            if ( finished.contains(pName) ) {
                continue;
            }

            int kill = conf.getInt(KDUserData.KILLS_KEY);

            if ( kill <= 0 ) {
                continue;
            }

            String uuidStr = file.getName().substring(0, file.getName().lastIndexOf("."));

            try {
                PlayerInfo info = new PlayerInfo(UUID.fromString(uuidStr), pName);
                map.put(info, kill);
            } catch ( Exception e ) {
                plugin.getLogger().warning("Failed to convert uuid '" + uuidStr + "'");
            }
        }

        return map;
    }

    /**
     * 全プレイヤーの指定した期間のキル数を取得します
     *
     * @param unit 取得したい期間
     * @return 全プレイヤーの指定した期間のキル数 (0は除く) 1件もない場合は空のMapが返される
     */
    public HashMap<PlayerInfo, Integer> getAllKills(TimeUnit unit) {

        // HashMap作成
        HashMap<PlayerInfo, Integer> dataMap = new HashMap<>();
        // Folderを取得
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        // 存在していない場合は空のMapを返す
        if ( !folder.exists() ) {
            return dataMap;
        }

        // cacheからロードし、2回目のファイルからのロードを避けるためListを作成しておく
        List<String> alreadyAddedUUID = new ArrayList<>();
        // cacheから読み込んで追加
        for ( KDUserData data : playerDataCache.values() ) {
            int kills = -1;
            if ( unit == TimeUnit.DAILY ) {
                kills = data.getKills(TimeUnit.DAILY);
            } else if ( unit == TimeUnit.MONTHLY ) {
                kills = data.getKills(TimeUnit.MONTHLY);
            } else if ( unit == TimeUnit.YEARLY ) {
                kills = data.getKills(TimeUnit.YEARLY);
            }

            // 0以下ならcontinue
            if ( kills <= 0 ) {
                continue;
            }
            // PlayerInfoを作成して追加
            dataMap.put(new PlayerInfo(data.getPlayer().getUniqueId(), data.getPlayer().getName()), kills);
            alreadyAddedUUID.add(data.getPlayer().getUniqueId().toString());
        }

        // 全ファイルをロードする
        for ( File file : folder.listFiles() ) {
            if ( !file.isFile() || !file.getName().endsWith(".yml") ) {
                continue;
            }
            String uuidStr = file.getName().substring(0, file.getName().lastIndexOf("."));
            // すでに実行済みならreturn
            if ( alreadyAddedUUID.contains(uuidStr) ) {
                continue;
            }

            // YamlConfigurationとしてロードする
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

            // プレイヤー名を取得
            String pName = conf.getString(KDUserData.LAST_NAME_KEY, null);

            // 最終キルが正しい時ではない場合は0なのでcontinue
            if ( !isCorrectKills(conf.getLong(KDUserData.LAST_KILL_KEY, 0L), unit) ) {
                continue;
            }

            // キル数を取得する
            int kills = -1;
            if ( unit == TimeUnit.DAILY ) {
                kills = conf.getInt(KDUserData.DAILY_KILLS_KEY, 0);
            } else if ( unit == TimeUnit.MONTHLY ) {
                kills = conf.getInt(KDUserData.MONTHLY_KILLS_KEY, 0);
            } else if ( unit == TimeUnit.YEARLY ) {
                kills = conf.getInt(KDUserData.YEARLY_KILLS_KEY, 0);
            }

            // キル数が0以下の場合はcontinue
            if ( kills <= 0 ) {
                continue;
            }

            // uuidをparseし、dataMapに追加する
            try {
                PlayerInfo info = new PlayerInfo(UUID.fromString(uuidStr), pName);
                dataMap.put(info, kills);
            } catch ( Exception e ) {
                // uuidのparseに失敗したらログを出す
                plugin.getLogger().warning("Failed to convert uuid '" + uuidStr + "'");
            }
        }

        // return
        return dataMap;
    }

    /**
     * 書き込まれている値が正しい値なのか、最終更新を元に判定します
     *
     * @param lastUpdate 最終更新のミリ秒
     * @param unit 期間
     * @return trueなら正しく、falseなら正しくない
     */
    private boolean isCorrectKills(long lastUpdate, TimeUnit unit) {
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        Date date = new Date(lastUpdate);
        cal.setTime(date);

        if ( unit == TimeUnit.DAILY ) {
            return !(cal.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR));
        } else if ( unit == TimeUnit.MONTHLY ) {
            return !(cal.get(Calendar.MONTH) != now.get(Calendar.MONTH) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR));
        } else if ( unit == TimeUnit.YEARLY ) {
            return !(cal.get(Calendar.YEAR) != now.get(Calendar.YEAR));
        }

        return false;
    }

    public enum TimeUnit {
        DAILY,
        MONTHLY,
        YEARLY
    }
}
