package net.azisaba.kdstatusreloaded.sql;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;

import java.util.logging.Logger;

/**
 * Safe auth config loader
 */
public class DBAuthConfig {
    protected static String host;
    protected static String port;
    protected static String database;
    protected static String user;
    protected static String password;

    public static void loadAuthConfig() {
        host = getConfigAsString("host");
        port = getConfigAsString("port");
        database = getConfigAsString("database");
        user = getConfigAsString("username");
        password = getConfigAsString("password");
    }

    public static HikariMySQLDatabase getDatabase(Logger logger, int maxPoolSize) {
        return new HikariMySQLDatabase(
                logger,
                maxPoolSize,
                host,
                port,
                database,
                user,
                password
        );
    }

    private static String getConfigAsString(String path) {
        return KDStatusReloaded.getPlugin().getConfig().getString(path);
    }
}
