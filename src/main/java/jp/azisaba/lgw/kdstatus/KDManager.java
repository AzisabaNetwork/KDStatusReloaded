package jp.azisaba.lgw.kdstatus;

import java.util.HashMap;

import org.bukkit.entity.Player;

import jp.azisaba.lgw.kdstatus.KillDeathDataContainer.TimeUnit;

/**
 * プレイヤーの戦績を取得したり設定したりするクラスです
 *
 * @deprecated {@link KDStatusReloaded#getKdDataContainer()} を使用してください
 * @author siloneco
 *
 */
@Deprecated
public class KDManager {

    private static KDStatusReloaded plugin;
    private static KillDeathDataContainer dataContainer;

    public static void init(KDStatusReloaded plugin, KillDeathDataContainer dataContainer) {
        KDManager.plugin = plugin;
        KDManager.dataContainer = dataContainer;
    }

    public static KDStatusReloaded getPlugin() {
        return plugin;
    }

    public static KDUserData getPlayerData(Player p, boolean registerIfNot) {
        return dataContainer.getPlayerData(p, registerIfNot);
    }

    public static KDUserData registerPlayer(Player p) {
        return dataContainer.registerPlayer(p);
    }

    public static void unRegisterPlayer(Player p, boolean save) {
        dataContainer.unRegisterPlayer(p, save);
    }

    public static void saveAllPlayerData(boolean async, boolean clear) {
        dataContainer.saveAllPlayerData(async, clear);
    }

    public static int getKills(Player p) {
        return dataContainer.getKills(p);
    }

    public static int getDeaths(Player p) {
        return dataContainer.getDeaths(p);
    }

    public static HashMap<PlayerInfo, Integer> getAllDailyKills() {
        return dataContainer.getAllKills(TimeUnit.DAILY);
    }

    public static HashMap<PlayerInfo, Integer> getAllMonthlyKills() {
        return dataContainer.getAllKills(TimeUnit.MONTHLY);
    }

    public static HashMap<PlayerInfo, Integer> getAllTotalKills() {
        return dataContainer.getAllTotalKills();
    }
}
