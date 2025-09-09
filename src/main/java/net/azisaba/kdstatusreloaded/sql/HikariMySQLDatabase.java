package net.azisaba.kdstatusreloaded.sql;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper class of HikariDataSource for MySQL
 */
@RequiredArgsConstructor
public class HikariMySQLDatabase {
    private final Logger logger;
    private final int maxPoolSize;
    private final String host, port, databaseName, user, password;

    @Getter
    private boolean initialized;
    private HikariDataSource hikari;

    public boolean isConnected() {
        if (hikari == null) return false;
        return hikari.isRunning();
    }

    /**
     * connect to database.
     */
    public void connect() {
        if (initialized) {
            logger.warning("Database is already initialized!");
            return;
        }
        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false",
                host,
                port,
                databaseName
        );

        HikariConfig config = new HikariConfig();
        config.setUsername(user);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        config.setMaximumPoolSize(maxPoolSize);
        config.setLeakDetectionThreshold(2000);
//        config.setConnectionInitSql("SELECT 1");
        config.setAutoCommit(true);
        config.setConnectionTimeout(1500); // TODO change this
        hikari = new HikariDataSource(config);
    }

    /**
     * close database connection
     */
    public void close() {
        hikari.close();
        initialized = false;
    }

    /**
     * reconnect database
     */
    public void reconnect() {
        close();
        connect();
    }

    /**
     * CAUTION: this method throws {@link SQLException} so need to catch it
     *
     * @return a connection of database
     * @throws SQLException from {@link HikariDataSource#getConnection()}
     */
    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    /**
     * get a connection (Safer than {@link #getConnection()})
     *
     * @return database connection. If failed, return null.
     */

    public Connection getConnectionOrNull() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get connection", e);
            return null;
        }
    }


    public PreparedStatement preparedStatement(@NonNull String sql) {
        Connection conn = getConnectionOrNull();
        if (conn == null) {
            logger.log(Level.SEVERE, "Failed to create preparedStatement: connection is null");
            return null;
        }
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create preparedStatement", e);
            return null;
        }
    }

    /**
     * execute a SQL statement
     *
     * @param sql           SQL statement
     * @param pstmtConsumer to process PreparedStatement (ex. {@link PreparedStatement#setString(int, String)})
     * @return result of execution. If failed, return null
     */

    public ResultSet executeQuery(@NonNull String sql, Consumer<PreparedStatement> pstmtConsumer) {
        // get a connection
        Connection conn = getConnectionOrNull();
        if (conn == null) {
            logger.warning("Failed to execute query: connection is null");
            return null;
        }

        // execute query
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (pstmtConsumer != null) {
                pstmtConsumer.accept(pstmt);
            }
            return pstmt.executeQuery();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to execute query", e);
            return null;
        }
    }

    /**
     * @param sql SQL statement
     * @return is succeeded
     */
    public boolean executeUpdate(String sql) {
        Connection conn = getConnectionOrNull();
        if (conn == null) {
            logger.severe("Failed to execute update: connection is null");
            return false;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to execute update", e);
            return false;
        }
    }

    public boolean isConnectionAlive() {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1");
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
