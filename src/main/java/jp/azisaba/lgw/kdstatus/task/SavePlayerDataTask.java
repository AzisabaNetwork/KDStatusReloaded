package jp.azisaba.lgw.kdstatus.task;

import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;

/**
 * プレイヤーのキルデス情報をセーブするタスク
 *
 * @author siloneco
 *
 */
public class SavePlayerDataTask extends BukkitRunnable {

    private final KDStatusReloaded plugin;
    @Getter
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
}
