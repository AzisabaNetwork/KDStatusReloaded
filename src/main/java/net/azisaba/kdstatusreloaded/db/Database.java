package net.azisaba.kdstatusreloaded.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jspecify.annotations.NonNull;

public class Database {
    private final HikariDataSource hikariDataSource;
    private Jdbi jdbi;
    private KDUserDataRepository kdUserDataRepository;

    public Database(String host, int port, String database, String username, String password) {
        // create HikariCP datasource
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
        config.setUsername(username);
        config.setPassword(password);
        hikariDataSource = new HikariDataSource(config);

        // jdbi initialization
        jdbi = Jdbi.create(hikariDataSource);
        kdUserDataRepository = jdbi.onDemand(KDUserDataRepository.class);

        // Todo: move this to correct place
        migration();
    }

    public void migration() {
        Flyway flyway = Flyway.configure(Database.class.getClassLoader())
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

    @NonNull
    public KDUserDataRepository kdUserDataRepository() {
        if(kdUserDataRepository == null) throw new RuntimeException("KDUserDataRepository was already closed.");
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
