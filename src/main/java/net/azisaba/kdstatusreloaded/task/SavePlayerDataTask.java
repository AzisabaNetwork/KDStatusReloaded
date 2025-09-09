package net.azisaba.kdstatusreloaded.task;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * プレイヤーのキルデス情報をセーブするタスク
 *
 * @author siloneco
 */
public class SavePlayerDataTask extends BukkitRunnable {

    private final KDStatusReloaded plugin;
    private long lastSavedTime = System.currentTimeMillis();

    public SavePlayerDataTask(KDStatusReloaded plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // 全プレイヤーのデータをセーブする
        plugin.getKdDataContainer().saveAllPlayerData(true, false);

        lastSavedTime = System.currentTimeMillis();
        plugin.getLogger().info("全プレイヤーデータをセーブしました。");
    }

    public long getLastSavedTime() {
        return lastSavedTime;
    }
}
