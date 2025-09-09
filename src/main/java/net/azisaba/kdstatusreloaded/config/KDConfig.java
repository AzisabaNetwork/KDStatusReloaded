package net.azisaba.kdstatusreloaded.config;

import de.exlll.configlib.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class KDConfig {
    public DatabaseConfig database = new DatabaseConfig();
    public KillDeathConfig world = new KillDeathConfig();

    @Configuration
    public static class DatabaseConfig {
        public String host = "127.0.0.1";
        public int port = 3306;
        public String dbName = "kdstatusreloaded";
        public String username = "kduser";
        public String password = "kdpass";
    }

    @Configuration
    public static class KillDeathConfig {
        public boolean showCountCancelled = true;
        public List<String> disableKillWorldList = Collections.singletonList("world");
        public List<String> disableDeathWorldList = Collections.singletonList("world");
    }
}
