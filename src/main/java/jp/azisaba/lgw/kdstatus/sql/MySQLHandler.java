package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.KDStatusReloaded;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLHandler {

    private Connection connection;

    private final String host = KDStatusReloaded.getPlugin().getConfig().getString("host");
    private final String port = KDStatusReloaded.getPlugin().getConfig().getString("port");;
    private final String database = KDStatusReloaded.getPlugin().getConfig().getString("database");;
    private final String user = KDStatusReloaded.getPlugin().getConfig().getString("username");;
    private final String password = KDStatusReloaded.getPlugin().getConfig().getString("password");;

    public boolean isConnected(){
        return (connection != null);
    }

    public void connect() throws SQLException {

        if(!isConnected())
            connection = DriverManager.getConnection("jdbc:mysql://" + host +":"+ port + "/" + database + "?useSSL=false",user,password );

    }

    public void close(){

        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public Connection getConnection() {
        return connection;
    }
}
