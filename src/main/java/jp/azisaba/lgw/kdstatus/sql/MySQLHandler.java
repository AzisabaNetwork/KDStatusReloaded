package jp.azisaba.lgw.kdstatus.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHandler {

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Internal method for connect MySQL
     *
     * @throws SQLException from {@link DriverManager#getConnection(String, String, String)}
     */
    public void connect() throws SQLException {
        if (isConnected()) return;
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

        if (isConnected()) {
            connection.close();
        }
        connection = null;
    }

}
