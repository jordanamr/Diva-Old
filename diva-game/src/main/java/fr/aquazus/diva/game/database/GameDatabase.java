package fr.aquazus.diva.game.database;

import fr.aquazus.diva.database.DivaDatabase;
import fr.aquazus.diva.database.generated.auth.tables.daos.AccountsDao;
import fr.aquazus.diva.database.generated.auth.tables.daos.CharactersDao;
import fr.aquazus.diva.database.generated.auth.tables.daos.RanksDao;
import fr.aquazus.diva.database.generated.auth.tables.daos.ServersDao;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class GameDatabase extends DivaDatabase {

    @Getter
    private DSLContext dsl;

    public GameDatabase(String server, String username, String password, String database, int poolSize) {
        super(server, username, password, database, poolSize);
    }

    public void connect() {
        try {
            super.connect();
            this.dsl = DSL.using(super.getPool(), SQLDialect.MARIADB);
            log.info("Successfully connected to game database.");
        } catch (SQLException ex) {
            log.error("A fatal error occured while connecting to the game database", ex);
            System.exit(-1);
        }
    }

    public Connection getConnection() {
        try {
            return super.getConnection();
        } catch (SQLException ex) {
            log.error("A fatal error occured while fetching a SQL connection from the game pool", ex);
            System.exit(-1);
            return null;
        }
    }
}
