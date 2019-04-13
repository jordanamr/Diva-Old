package fr.aquazus.diva.database.game;

import fr.aquazus.diva.database.DivaDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class GameDatabase extends DivaDatabase {

    private static GameDatabase INSTANCE = null;

    public static GameDatabase getInstance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    @Getter
    private DSLContext dsl;
    @Getter
    private ExperienceTable experienceTable;

    public GameDatabase(String server, String username, String password, String database, int poolSize) {
        super(server, username, password, database, poolSize);
        INSTANCE = this;
    }

    public void connect() {
        try {
            super.connect();
            this.dsl = DSL.using(super.getPool(), SQLDialect.MARIADB);
            log.info("Successfully connected to game database.");
        } catch (Exception ex) {
            log.error("A fatal error occurred while connecting to the game database", ex);
            System.exit(-1);
        }
    }

    public void load() {
        log.info("Loading experience table...");
        experienceTable = new ExperienceTable(dsl);
    }

    public Connection getConnection() {
        try {
            return super.getConnection();
        } catch (SQLException ex) {
            log.error("A fatal error occurred while fetching a SQL connection from the game pool", ex);
            System.exit(-1);
            return null;
        }
    }
}
