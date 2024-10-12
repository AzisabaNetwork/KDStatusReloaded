package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHandler {

    @Getter
    private Connection connection;

    private final String host = KDStatusReloaded.getPlugin().getConfig().getString("host");
    private final String port = KDStatusReloaded.getPlugin().getConfig().getString("port");
    private final String database = KDStatusReloaded.getPlugin().getConfig().getString("database");
    private final String user = KDStatusReloaded.getPlugin().getConfig().getString("username");
    private final String password = KDStatusReloaded.getPlugin().getConfig().getString("password");

    public boolean isConnected(){
        return (connection != null);
    }

    /**
     * Internal method for connect MySQL
     * @throws SQLException from {@link DriverManager#getConnection(String, String, String)}
     */
    public void connect() throws SQLException {
        if(isConnected()) return;
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false",
                DBAuthConfig.getHost(),
                DBAuthConfig.getPort(),
                DBAuthConfig.getDatabase()
        );
        connection = DriverManager.getConnection(jdbcUrl, DBAuthConfig.getUser(), DBAuthConfig.getPassword());
    }

    public void reconnect() throws SQLException {
        if(isConnected()) close();
        connect();
    }

    public void close() throws SQLException {

        if(isConnected()) {
            connection.close();
        }

    }

}
