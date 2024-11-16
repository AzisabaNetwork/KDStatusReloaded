package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.TestSize;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestSize.LARGE)
public class ConnectionTest {
    @Test
    public void ConnectToDatabase() throws InterruptedException {
        Random rnd = new Random();
        HikariMySQLDatabase db = new HikariMySQLDatabase(
                Logger.getLogger("DBConnectionTest"),
                10,
                "localhost",
                "3306",
                "kdstatusreloaded",
                "root",
                "mariadb"
        );
        db.connect();
        for(int i=0;i<20;i++) {
            assertTrue(db.isConnectionAlive());
            System.out.println("Connection is alive! " + i);
            try(Connection conn = db.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT 1")) {
                assertTrue(pstmt.executeQuery().next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Thread.sleep(rnd.nextInt(2000));
        }
        db.close();
    }
}
