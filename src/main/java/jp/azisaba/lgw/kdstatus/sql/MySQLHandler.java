package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLHandler {

    private Connection connection;

    private final String host = KDStatusReloaded.getPlugin().getConfig().getString("host");
    private final String port = KDStatusReloaded.getPlugin().getConfig().getString("port");
    private final String database = KDStatusReloaded.getPlugin().getConfig().getString("database");
    private final String user = KDStatusReloaded.getPlugin().getConfig().getString("username");
    private final String password = KDStatusReloaded.getPlugin().getConfig().getString("password");

    public boolean isConnected(){
        return (connection != null);
    }

    public void connect() throws SQLException {

        if(!isConnected())
            connection = DriverManager.getConnection("jdbc:mysql://" + host +":"+ port + "/" + database + "?useSLL=false",user,password );
    }

    public void reconnect() throws SQLException {
        close();
        connect();
    }

    public void close(){

        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        connection = null;
    }

    public Connection getConnection() {
        return connection;
    }
}
