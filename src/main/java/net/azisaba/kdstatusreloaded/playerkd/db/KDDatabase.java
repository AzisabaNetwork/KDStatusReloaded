package net.azisaba.kdstatusreloaded.playerkd.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.azisaba.kdstatusreloaded.config.KDConfig;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public class KDDatabase {
    private static final Logger logger = LoggerFactory.getLogger(KDDatabase.class);
    private final HikariDataSource hikariDataSource;
    private Jdbi jdbi;
    private KDUserDataRepository kdUserDataRepository;
    private final File tmpFolder;

    public KDDatabase(KDConfig.DatabaseConfig dbConfig, File pluginFolder) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        tmpFolder = new File(pluginFolder, "migration_tmp");
        if(!tmpFolder.exists()) tmpFolder.mkdirs();

        // create HikariCP datasource
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mariadb://%s:%d/%s", dbConfig.host, dbConfig.port, dbConfig.dbName));
        config.setUsername(dbConfig.username);
        config.setPassword(dbConfig.password);
        hikariDataSource = new HikariDataSource(config);

        // jdbi initialization
        jdbi = Jdbi.create(hikariDataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        kdUserDataRepository = jdbi.onDemand(KDUserDataRepository.class);

        migrate();
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

    public void migrate() {
        // v0
        jdbi.useHandle(handle ->
                handle.execute(
                        "CREATE TABLE IF NOT EXISTS kill_death_data (" +
                                "    uuid VARCHAR(64) NOT NULL," +
                                "    name VARCHAR(36) NOT NULL," +
                                "    kills INT DEFAULT 0," +
                                "    deaths INT DEFAULT 0 ," +
                                "    daily_kills INT DEFAULT 0," +
                                "    monthly_kills INT DEFAULT 0," +
                                "    yearly_kills INT DEFAULT 0," +
                                "    last_updated BIGINT DEFAULT -1" +
                                ");"
                )
        );

        // v1
        jdbi.useHandle(handle ->
                handle.execute(
                        "ALTER TABLE kill_death_data " +
                                "ADD UNIQUE KEY IF NOT EXISTS uniq_uuid (uuid);"
                )
        );
    }
}
