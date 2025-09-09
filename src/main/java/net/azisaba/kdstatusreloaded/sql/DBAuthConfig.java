package net.azisaba.kdstatusreloaded.sql;

import net.azisaba.kdstatusreloaded.KDStatusReloaded;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.logging.Logger;

/**
 * Safe auth config loader
 */
public class DBAuthConfig {
    @Getter(AccessLevel.PROTECTED)
    private static String host;
    @Getter(AccessLevel.PROTECTED)
    private static String port;
    @Getter(AccessLevel.PROTECTED)
    private static String database;
    @Getter(AccessLevel.PROTECTED)
    private static String user;
    @Getter(AccessLevel.PROTECTED)
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
