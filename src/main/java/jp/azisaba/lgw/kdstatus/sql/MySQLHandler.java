package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLHandler {

    @Getter
    private Connection connection;

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
        close();
        connect();
    }

    public void close() throws SQLException {

        if(isConnected()) {
            connection.close();
        }
        connection = null;
    }

}
