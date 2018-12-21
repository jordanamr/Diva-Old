package fr.aquazus.diva.database;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DivaDatabase {

    private String url;
    private MariaDbPoolDataSource pool;

    protected DivaDatabase(String server, String username, String password, String database, int poolSize) {
        this.url = String.format("jdbc:mariadb://%s/%s?user=%s&password=%s&maxPoolSize=%s", server, database, username, password, poolSize);
    }

    protected void connect() throws SQLException {
        pool = new MariaDbPoolDataSource(url);
    }

    protected Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    protected MariaDbPoolDataSource getPool() {
        return pool;
    }
}
