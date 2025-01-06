package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;

import java.util.logging.Logger;

/**
 * Safe auth config loader
 */
public class DBAuthConfig {
    private static String host;
    private static String port;
    private static String database;
    private static String user;

    protected static String getHost() {
        return host;
    }

    protected static String getPort() {
        return port;
    }

    protected static String getDatabase() {
        return database;
    }

    protected static String getUser() {
        return user;
    }

    protected static String getPassword() {
        return password;
    }

    private static String password;

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
