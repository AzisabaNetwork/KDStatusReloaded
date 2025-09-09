package net.azisaba.kdstatusreloaded.sql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHandler {

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    /**
     * Internal method for connect MySQL
     *
     * @throws SQLException from {@link DriverManager#getConnection(String, String, String)}
     */
    public void connect() throws SQLException {
        if (isConnected()) return;
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false",
                DBAuthConfig.host,
                DBAuthConfig.port,
                DBAuthConfig.database
        );
        connection = DriverManager.getConnection(jdbcUrl, DBAuthConfig.user, DBAuthConfig.password);
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

    public Connection getConnection() {
        return connection;
    }
}
