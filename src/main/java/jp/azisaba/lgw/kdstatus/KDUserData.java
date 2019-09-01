package jp.azisaba.lgw.kdstatus;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import jp.azisaba.lgw.kdstatus.KillDeathDataContainer.TimeUnit;

public class KDUserData {

    private final Player p;

    private YamlConfiguration conf;
    private File file;

    public static final String KILLS_KEY = "Kills";
    public static final String DEATHS_KEY = "Deaths";
    public static final String LAST_KILL_KEY = "LastKill";
    public static final String LAST_NAME_KEY = "LastName";
    public static final String DAILY_KILLS_KEY = "DailyKill";
    public static final String MONTHLY_KILLS_KEY = "MonthlyKill";
    public static final String YEARLY_KILLS_KEY = "YearlyKill";

    public KDUserData(Player p) {
        this.p = p;
        loadFile();
    }

    /**
     * 対象プレイヤーを取得する
     *
     * @return 対象プレイヤー
     */
    public Player getPlayer() {
        return p;
    }

    /**
     * キル数を追加する
     *
     * @param num 追加するキル数
     */
    public void addKill(int num) {

        if ( conf.get(KILLS_KEY) == null ) {
            conf.set(KILLS_KEY, num);
            return;
        }

        conf.set(KILLS_KEY, conf.getInt(KILLS_KEY) + num);

        addKills(TimeUnit.DAILY, num);
        addKills(TimeUnit.MONTHLY, num);
        addKills(TimeUnit.YEARLY, num);

        updateLastKillLong();
    }

    /**
     * デス数を追加する
     *
     * @param num 追加するデス数
     */
    public void addDeath(int num) {
        if ( conf.get(DEATHS_KEY) == null ) {
            conf.set(DEATHS_KEY, num);
            return;
        }

        conf.set(DEATHS_KEY, conf.getInt(DEATHS_KEY) + num);
    }

    /**
     * 最終アップデートをミリ秒で取得します
     *
     * @return 取得したミリ秒。なければ-1
     */
    public long getLastKillLong() {
        return conf.getLong(LAST_KILL_KEY, -1);
    }

    /**
     * 最終アップデートを更新します
     */
    public void updateLastKillLong() {
        conf.set(LAST_KILL_KEY, System.currentTimeMillis());
    }

    /**
     * トータルキル数を取得します
     *
     * @return 取得したキル数
     */
    public int getKills() {
        return conf.getInt(KILLS_KEY, 0);
    }

    /**
     * トータルデス数を取得します
     *
     * @return 取得したデス数
     */
    public int getDeaths() {
        return conf.getInt(DEATHS_KEY, 0);
    }

    /**
     * 指定した期間のキル数を追加します。これはTotalとは別の値として扱われます
     *
     * @param unit キル数を追加したい期間
     * @param num  追加するキル数
     */
    public void addKills(TimeUnit unit, int num) {
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        Date date = new Date(getLastKillLong());
        cal.setTime(date);

        if ( unit == TimeUnit.DAILY ) {
            if ( cal.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
                conf.set(DAILY_KILLS_KEY, num);
            } else {
                conf.set(DAILY_KILLS_KEY, conf.getInt(DAILY_KILLS_KEY, 0) + num);
            }
        } else if ( unit == TimeUnit.MONTHLY ) {
            if ( cal.get(Calendar.MONTH) != now.get(Calendar.MONTH) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
                conf.set(MONTHLY_KILLS_KEY, num);
            } else {
                conf.set(MONTHLY_KILLS_KEY, conf.getInt(MONTHLY_KILLS_KEY, 0) + num);
            }
        } else if ( unit == TimeUnit.YEARLY ) {
            if ( cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
                conf.set(YEARLY_KILLS_KEY, num);
            } else {
                conf.set(YEARLY_KILLS_KEY, conf.getInt(YEARLY_KILLS_KEY, 0) + num);
            }
        }
    }

    /**
     * 指定した期間のキル数を取得します
     *
     * @param unit 取得したい期間
     * @return 取得したキル数。なければ0
     */
    public int getKills(TimeUnit unit) {
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        Date date = new Date(getLastKillLong());

        cal.setTime(date);

        if ( unit == TimeUnit.DAILY ) {
            if ( cal.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
                return 0;
            }

            return conf.getInt(DAILY_KILLS_KEY, 0);
        } else if ( unit == TimeUnit.MONTHLY ) {
            if ( cal.get(Calendar.MONTH) != now.get(Calendar.MONTH) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
                return 0;
            }

            return conf.getInt(MONTHLY_KILLS_KEY, 0);
        } else if ( unit == TimeUnit.YEARLY ) {
            if ( cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
                return 0;
            }

            return conf.getInt(YEARLY_KILLS_KEY, 0);
        }

        return -1;
    }

    /**
     * YamlConfugurationをファイルにセーブします
     *
     * @param async 非同期で実行するかどうか
     */
    public void saveData(boolean async) {

        long start = System.currentTimeMillis();

        if ( async ) {
            new Thread(() -> {
                saveData(false);
            }).start();
            return;
        }

        boolean success = save();

        if ( success ) {
            long end = System.currentTimeMillis();
            KDStatusReloaded.getPlugin().getLogger().info("Saved " + p.getName() + "'s player data (" + (end - start) + " ms)");
        } else {
            if ( KDStatusReloaded.getPlugin().getPluginConfig().showLogInConsole ) {
                KDStatusReloaded.getPlugin().getLogger().warning("Failed to save " + p.getName() + "'s player data");
            }
        }
    }

    /**
     * ファイルからデータをロードします。ない場合は作成します
     */
    private void loadFile() {

        long start = System.currentTimeMillis();

        // フォルダを取得し、なければ作成する
        File folder = new File(KDStatusReloaded.getPlugin().getDataFolder(), "PlayerData");
        if ( !folder.exists() ) {
            folder.mkdirs();
        }

        // UUIDを元にファイルを取得、なければ作成する
        file = new File(folder, p.getUniqueId().toString() + ".yml");
        if ( !file.exists() ) {
            try {
                file.createNewFile();
            } catch ( IOException e ) {
                // 失敗したらログを出力してreturn
                e.printStackTrace();
                return;
            }
        }

        // YamlConfigurationとしてロードする
        conf = YamlConfiguration.loadConfiguration(file);

        // 名前を設定する
        conf.set(LAST_NAME_KEY, p.getName());
        // セーブ
        saveData(true);

        long end = System.currentTimeMillis();

        // ログを出力
        KDStatusReloaded.getPlugin().getLogger().info("Loaded " + p.getName() + "'s data (" + (end - start) + "ms)");
    }

    /**
     * ファイルをセーブします
     *
     * @return セーブできたかどうか
     */
    private boolean save() {
        try {
            conf.save(file);
            return true;
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * プレイヤーのDailyキルを追加します
     *
     * @deprecated {@link #addKills(TimeUnit, int)} を使用してください
     * @param num 追加するキル数
     */
    @Deprecated
    public void addDailyKill(int num) {
        addKills(TimeUnit.DAILY, num);
    }

    /**
     * プレイヤーのMonthlyキルを追加します
     *
     * @deprecated {@link #addKills(TimeUnit, int)} を使用してください
     * @param num 追加するキル数
     */
    @Deprecated
    public void addMonthlyKill(int num) {
        addKills(TimeUnit.MONTHLY, num);
    }

    /**
     * プレイヤーのYearlyキルを追加します
     *
     * @deprecated {@link #addKills(TimeUnit, int)} を使用してください
     * @param num 追加するキル数
     */
    @Deprecated
    public void addYearlyKill(int num) {
        addKills(TimeUnit.YEARLY, num);
    }

    /**
     * プレイヤーのDailyキルを取得します
     *
     * @deprecated {@link #getKills(TimeUnit)} を使用してください
     * @return プレイヤーのDailyキル数
     */
    @Deprecated
    public int getDailyKills() {
        return getKills(TimeUnit.DAILY);
    }

    /**
     * プレイヤーのMonthlyキルを取得します
     *
     * @deprecated {@link #getKills(TimeUnit)} を使用してください
     * @return プレイヤーのMonthlyキル数
     */
    @Deprecated
    public int getMonthlyKills() {
        return getKills(TimeUnit.MONTHLY);
    }

    /**
     * プレイヤーのYearlyキルを取得します
     *
     * @deprecated {@link #getKills(TimeUnit)} を使用してください
     * @return プレイヤーのYearlyキル数
     */
    @Deprecated
    public int getYearlyKills() {
        return getKills(TimeUnit.YEARLY);
    }

}
