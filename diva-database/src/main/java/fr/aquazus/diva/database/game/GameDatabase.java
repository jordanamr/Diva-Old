package fr.aquazus.diva.database.game;

import fr.aquazus.diva.database.DivaDatabase;
import fr.aquazus.diva.database.generated.game.tables.records.ExperienceTableRecord;
import fr.aquazus.diva.database.generated.game.tables.records.MapsRecord;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static fr.aquazus.diva.database.generated.game.Tables.MAPS;

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
    @Getter
    private Map<Integer, String[]> mapsData;

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
        log.info("Loading maps...");
        mapsData = Collections.synchronizedMap(new HashMap<>());
        for (MapsRecord mapRecord : dsl.selectFrom(MAPS).fetch()) {
            mapsData.put(mapRecord.getId(), new String[]{mapRecord.getDate(), mapRecord.getKey()});
        }
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
