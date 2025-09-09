package net.azisaba.kdstatusreloaded.playerkd.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.azisaba.kdstatusreloaded.config.KDConfig;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class KDDatabase {
    private final HikariDataSource hikariDataSource;
    private Jdbi jdbi;
    private KDUserDataRepository kdUserDataRepository;

    public KDDatabase(KDConfig.DatabaseConfig dbConfig) {
        // create HikariCP datasource
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", dbConfig.host, dbConfig.port, dbConfig.dbName));
        config.setUsername(dbConfig.username);
        config.setPassword(dbConfig.password);
        hikariDataSource = new HikariDataSource(config);

        // jdbi initialization
        jdbi = Jdbi.create(hikariDataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        kdUserDataRepository = jdbi.onDemand(KDUserDataRepository.class);

        migration();
    }

    public void migration() {
        Flyway flyway = Flyway.configure(KDDatabase.class.getClassLoader())
                .baselineVersion("0")
                .baselineOnMigrate(true)
                .dataSource(hikariDataSource)
                .locations("queries/migrations/mysql")
                .validateMigrationNaming(true)
                .validateOnMigrate(true)
                .load();

        flyway.repair();
        flyway.migrate();
    }

    public KDUserDataRepository kdUserDataRepository() {
        if (kdUserDataRepository == null) throw new RuntimeException("KDUserDataRepository was already closed.");
        return kdUserDataRepository;
    }

    public void shutdown() {
        // for safety
        jdbi = null;
        kdUserDataRepository = null;

        // close datasource
        hikariDataSource.close();
    }
}
