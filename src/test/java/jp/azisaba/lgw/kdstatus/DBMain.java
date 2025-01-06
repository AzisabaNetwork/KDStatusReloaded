package jp.azisaba.lgw.kdstatus;

import jp.azisaba.lgw.kdstatus.sql.HikariMySQLDatabase;
import jp.azisaba.lgw.kdstatus.sql.KillRankingData;
import jp.azisaba.lgw.kdstatus.sql.PlayerDataMySQLController;
import jp.azisaba.lgw.kdstatus.utils.TimeUnit;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DBMain {
    public static void main(String[] args) {
        // init logger
        Logger testLogger = Logger.getLogger("DBMain-Test");

        // -- connect db
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
        // -- start
        PlayerDataMySQLController controller = new PlayerDataMySQLController(db, testLogger);
        List<KillRankingData> dataList = controller.getTopKillRankingData(TimeUnit.LIFETIME, 10);
        for(KillRankingData data: dataList) {
            testLogger.info("Name: " + data.name());
        }
        int rank = controller.getRank(UUID.fromString("e76cae7b-dc41-40f9-86bb-01afc463e66c"), TimeUnit.LIFETIME);
        testLogger.info("Ranking: " + rank);

        // -- fin
        db.close();
    }
}
