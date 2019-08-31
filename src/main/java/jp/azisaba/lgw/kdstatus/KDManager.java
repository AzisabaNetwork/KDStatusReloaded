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

public class KDManager {

    private static KDStatusReloaded plugin;

    private static HashMap<Player, KDUserData> playerDataMap = new HashMap<>();

    public static void init(KDStatusReloaded plugin) {
        KDManager.plugin = plugin;
    }

    public static KDStatusReloaded getPlugin() {
        return plugin;
    }

    public static KDUserData getPlayerData(Player p, boolean registerIfNot) {

        if ( playerDataMap.containsKey(p) ) {
            return playerDataMap.get(p);
        }

        if ( !registerIfNot ) {
            return null;
        }

        return registerPlayer(p);
    }

    public static KDUserData registerPlayer(Player p) {
        if ( p == null ) {
            return null;
        }

        if ( playerDataMap.containsKey(p) ) {
            return playerDataMap.get(p);
        }

        KDUserData data = new KDUserData(p);
        playerDataMap.put(p, data);

        return data;
    }

    public static void unRegisterPlayer(Player p, boolean save) {
        if ( p == null ) {
            return;
        }

        if ( playerDataMap.containsKey(p) ) {

            if ( save ) {
                playerDataMap.get(p).saveData(true);
            }

            playerDataMap.remove(p);
        }
    }

    public static void saveAllPlayerData(boolean async, boolean clear) {
        playerDataMap.keySet().forEach(key -> {
            playerDataMap.get(key).saveData(async);
        });

        if ( clear ) {
            playerDataMap.clear();
        }
    }

    public static int getKills(Player p) {
        return registerPlayer(p).getKills();
    }

    public static int getDeaths(Player p) {
        return registerPlayer(p).getDeaths();
    }

    public static HashMap<PlayerInfo, Integer> getAllDailyKills() {

        HashMap<PlayerInfo, Integer> map = new HashMap<>();
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        if ( !folder.exists() ) {
            return map;
        }

        List<String> finished = new ArrayList<>();
        for ( KDUserData data : playerDataMap.values() ) {
            int dailyKill = data.getDailyKills();
            if ( dailyKill <= 0 ) {
                continue;
            }
            map.put(new PlayerInfo(data.getPlayer().getUniqueId(), data.getPlayer().getName()), dailyKill);
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

            if ( !isCorrectDailyKills(conf.getLong(KDUserData.LAST_KILL_KEY, 0L)) ) {
                continue;
            }

            int kill = conf.getInt(KDUserData.DAILY_KILLS_KEY, 0);

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

    public static HashMap<PlayerInfo, Integer> getAllMonthlyKills() {

        HashMap<PlayerInfo, Integer> map = new HashMap<>();
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        if ( !folder.exists() ) {
            return map;
        }

        List<String> finished = new ArrayList<>();
        for ( KDUserData data : playerDataMap.values() ) {
            int monthlyKill = data.getMonthlyKills();
            if ( monthlyKill <= 0 ) {
                continue;
            }
            map.put(new PlayerInfo(data.getPlayer().getUniqueId(), data.getPlayer().getName()), monthlyKill);
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

            if ( !isCorrectMonthlyKills(conf.getLong(KDUserData.LAST_KILL_KEY, 0L)) ) {
                continue;
            }

            int kill = conf.getInt(KDUserData.MONTHLY_KILLS_KEY, 0);

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

    public static HashMap<PlayerInfo, Integer> getAllTotalKills() {

        HashMap<PlayerInfo, Integer> map = new HashMap<>();
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        if ( !folder.exists() ) {
            return map;
        }

        List<String> finished = new ArrayList<>();
        for ( KDUserData data : playerDataMap.values() ) {
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

    private static boolean isCorrectDailyKills(long lastUpdate) {
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        Date date = new Date(lastUpdate);

        cal.setTime(date);

        if ( cal.get(Calendar.DATE) != now.get(Calendar.DATE) || cal.get(Calendar.MONTH) != now.get(Calendar.MONTH)
                || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
            return false;
        }

        return true;
    }

    private static boolean isCorrectMonthlyKills(long lastUpdate) {
        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        Date date = new Date(lastUpdate);

        cal.setTime(date);

        if ( cal.get(Calendar.MONTH) != now.get(Calendar.MONTH) || cal.get(Calendar.YEAR) != now.get(Calendar.YEAR) ) {
            return false;
        }

        return true;
    }
}
