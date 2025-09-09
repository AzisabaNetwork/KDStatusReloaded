package net.azisaba.kdstatusreloaded;

import de.exlll.configlib.YamlConfigurations;
import net.azisaba.kdstatusreloaded.commands.KDSCommands;
import net.azisaba.kdstatusreloaded.config.KDConfig;
import net.azisaba.kdstatusreloaded.playerkd.PlayerKD;
import net.azisaba.kdstatusreloaded.playerkd.db.KDDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.stream.Collectors;

public class KDStatusReloaded extends JavaPlugin {
    private static final Logger logger = LoggerFactory.getLogger(KDStatusReloaded.class);
    private static KDStatusReloaded plugin;
    protected File configFile;
    protected KDDatabase kdDatabase;
    protected KDConfig kdConfig;
    protected PlayerKD playerKd;

    public static KDStatusReloaded getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {

        plugin = this;
        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            YamlConfigurations.save(configFile.toPath(), KDConfig.class, new KDConfig());
            logger.error("設定ファイルを編集してから、再度有効化してください。");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        kdConfig = YamlConfigurations.load(configFile.toPath(), KDConfig.class);
        kdDatabase = new KDDatabase(kdConfig.database);

        playerKd = new PlayerKD(this);

        KDSCommands.init(this);

        // If player already in server, load data for each player
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            playerKd.loadAll(Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList()));
        }

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (playerKd != null) playerKd.onDisable();
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public KDConfig getPluginConfig() {
        return kdConfig;
    }

    public PlayerKD getPlayerKd() {
        return playerKd;
    }
}
