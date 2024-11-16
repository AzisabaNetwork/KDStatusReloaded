package jp.azisaba.lgw.kdstatus.sql;

import jp.azisaba.lgw.kdstatus.TestSize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

@Tag(TestSize.LARGE)
public class RankingTest {
    private static HikariMySQLDatabase db;
    @BeforeAll
    public static void setup() {
        db = new HikariMySQLDatabase(
                Logger.getLogger("DBConnectionTest"),
                10,
                "localhost",
                "3306",
                "kdstatusreloaded",
                "root",
                "mariadb"
        );
        db.connect();
    }

    @AfterAll
    public static void cleanup() {
        db.close();
    }

    @Test
    public void TestDailyRanking() {}
}
