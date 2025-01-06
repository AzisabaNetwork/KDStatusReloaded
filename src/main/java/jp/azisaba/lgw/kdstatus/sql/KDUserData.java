package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class KDUserData {

    private final UUID uuid;

    private YamlConfiguration conf;
    private File file;

    public static final String KILLS_KEY = "Kills";
    public static final String DEATHS_KEY = "Deaths";
    public static final String LAST_KILL_KEY = "LastKill";
    public static final String LAST_NAME_KEY = "LastName";
    public static final String DAILY_KILLS_KEY = "DailyKill";
    public static final String MONTHLY_KILLS_KEY = "MonthlyKill";
    public static final String YEARLY_KILLS_KEY = "YearlyKill";

    private String name;
    private int totalKills, dailyKills, monthlyKills, yearlyKills;
    private int deaths;
    private long lastUpdated;

    protected KDUserData(Player p) {
        this.uuid = p.getUniqueId();
        loadFile();

        // 名前を設定する
        name = p.getName();
    }

    protected KDUserData(UUID uuid) {
        this.uuid = uuid;
        loadFile();

        fixCorrectValue();
    }

    protected KDUserData(UUID uuid, String name, int totalKills, int deaths, int dailyKills, int monthlyKills, int yearlyKills, long lastUpdated) {
        this.uuid = uuid;
        this.name = name;
        this.totalKills = totalKills;
        this.dailyKills = dailyKills;
        this.monthlyKills = monthlyKills;
        this.yearlyKills = yearlyKills;
        this.deaths = deaths;
        this.lastUpdated = lastUpdated;

        fixCorrectValue();
    }

    /**
     * キル数を追加する
     *
     * @param num 追加するキル数
     */
    public void addKill(int num) {
        totalKills += num;

        fixCorrectValue();
        dailyKills += num;
        monthlyKills += num;
        yearlyKills += num;

        updateLastUpdated();
    }

    /**
     * デス数を追加する
     *
     * @param num 追加するデス数
     */
    public void addDeath(int num) {
        fixCorrectValue();
        deaths += num;

        updateLastUpdated();
    }

    /**
     * 最終アップデートをミリ秒で取得します
     *
     * @return 取得したミリ秒。なければ-1
     */
    public long getLastUpdated() {
        return lastUpdated;
    }

    /**
     * 最終アップデートを更新します
     */
    public void updateLastUpdated() {
        lastUpdated = System.currentTimeMillis();
    }

    /**
     * トータルデス数を取得します
     *
     * @return 取得したデス数
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * 指定した期間のキル数を追加します。これはTotalとは別の値として扱われます
     *
     * @param unit キル数を追加したい期間
     * @param num  追加するキル数
     */
    public void addKills(TimeUnit unit, int num) {
        fixCorrectValue();

        if (unit == TimeUnit.DAILY) {
            dailyKills += num;
        } else if (unit == TimeUnit.MONTHLY) {
            monthlyKills += num;
        } else if (unit == TimeUnit.YEARLY) {
            yearlyKills += num;
        }

        updateLastUpdated();
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * 指定した期間のキル数を取得します
     *
     * @param unit 取得したい期間
     * @return 取得したキル数。なければ0
     */
    public int getKills(@NotNull TimeUnit unit) {
        fixCorrectValue();

        if (unit == TimeUnit.LIFETIME) {
            return totalKills;
        } else if (unit == TimeUnit.DAILY) {
            return dailyKills;
        } else if (unit == TimeUnit.MONTHLY) {
            return monthlyKills;
        } else if (unit == TimeUnit.YEARLY) {
            return yearlyKills;
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

        if (async) {
            new Thread(() -> saveData(false)).start();
            return;
        }

        boolean success = KDStatusReloaded.getPlugin().getKdDataContainer().savePlayerData(this);

        if (success) {
            if (KDStatusReloaded.getPlugin().getPluginConfig().showLogInConsole) {
                long end = System.currentTimeMillis();
                KDStatusReloaded.getPlugin().getLogger().info("Saved " + name + "'s player data (" + (end - start) + " ms)");
            }
        } else {
            KDStatusReloaded.getPlugin().getLogger().warning("Failed to save " + name + "'s player data");
        }
    }

    public void fixCorrectValue() {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastUpdated);

        if (now.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
            dailyKills = 0;
            monthlyKills = 0;
            yearlyKills = 0;
        } else if (now.get(Calendar.MONTH) != cal.get(Calendar.MONTH)) {
            dailyKills = 0;
            monthlyKills = 0;
        } else if (now.get(Calendar.DATE) != cal.get(Calendar.DATE)) {
            dailyKills = 0;
        }
    }

    /**
     * ファイルからデータをロードします。ない場合は作成します
     */
    private void loadFile() {

        long start = System.currentTimeMillis();

        // フォルダを取得し、なければ作成する
        File folder = new File(KDStatusReloaded.getPlugin().getDataFolder(), "PlayerData");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // UUIDを元にファイルを取得、なければ作成する
        file = new File(folder, uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // 失敗したらログを出力してreturn
                e.printStackTrace();
                return;
            }
        }

        // YamlConfigurationとしてロードする
        conf = YamlConfiguration.loadConfiguration(file);

        // 各値をロード
        name = conf.getString(LAST_NAME_KEY, null);
        totalKills = conf.getInt(KILLS_KEY, 0);
        dailyKills = conf.getInt(DAILY_KILLS_KEY, 0);
        monthlyKills = conf.getInt(MONTHLY_KILLS_KEY, 0);
        yearlyKills = conf.getInt(YEARLY_KILLS_KEY, 0);
        deaths = conf.getInt(DEATHS_KEY, 0);
        lastUpdated = conf.getLong(LAST_KILL_KEY, -1);

        long end = System.currentTimeMillis();

        // ログを出力
        KDStatusReloaded.getPlugin().getLogger().info("Loaded " + name + "'s data (" + (end - start) + "ms)");
    }
}
