package org.topository.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DbHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbHelper.class);

    private static final DbHelper INSTANCE = new DbHelper();
    private BasicDataSource h2DataSource;

    private DbHelper() {
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().getDataSource().getConnection();
    }

    public DataSource getDataSource() {
        return h2DataSource;
    }

    public static DbHelper getInstance() {
        return DbHelper.INSTANCE;
    }

    public void init() {
        DbHelper.LOGGER.debug("Loading properties");
        final Properties properties = new Properties();
        properties.put("db_path", "./target/david");
        properties.put("db.user", "david");
        properties.put("db_password", "password");
        try {
            properties.load(getClass().getResourceAsStream("/app.properties"));
        } catch (final IOException e) {
            DbHelper.LOGGER.error("Failed to load properties", e);
        }
        DbHelper.LOGGER.debug("Creating datasource");
        h2DataSource = new BasicDataSource();
        h2DataSource.setDriverClassName("org.h2.Driver");
        h2DataSource.setUrl("jdbc:h2:" + properties.getProperty("db.path"));
        h2DataSource.setUsername(properties.getProperty("db.user"));
        h2DataSource.setPassword(properties.getProperty("db.password"));

        DbHelper.LOGGER.debug("Executing Flyway");
        Flyway flyway = Flyway.configure().dataSource(h2DataSource).load();
        flyway.repair();
        flyway.migrate();

    }

    public void close() {
        if (h2DataSource != null) {
            DbHelper.LOGGER.debug("Closing data source");
            try {
                h2DataSource.close();
            } catch (final SQLException e) {
                DbHelper.LOGGER.debug("Failed to close data source", e);
            }
        } else {
            DbHelper.LOGGER.debug("Data source is null");
        }
    }

    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
                DbHelper.LOGGER.info("Closed the data source");
            }
        }, "DbHelper-0"));
    }
}
