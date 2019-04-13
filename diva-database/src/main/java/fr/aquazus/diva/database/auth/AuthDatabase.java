package fr.aquazus.diva.database.auth;

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
public class AuthDatabase extends DivaDatabase {

    private static AuthDatabase INSTANCE = null;

    public static AuthDatabase getInstance() {
        if (INSTANCE == null) throw new IllegalStateException();
        return INSTANCE;
    }

    @Getter
    private DSLContext dsl;
    @Getter
    private AccountsDao accountsDao;
    @Getter
    private RanksDao ranksDao;
    @Getter
    private ServersDao serversDao;
    @Getter
    private CharactersDao charactersDao;

    public AuthDatabase(String server, String username, String password, String database, int poolSize) {
        super(server, username, password, database, poolSize);
        INSTANCE = this;
    }

    public void connect() {
        try {
            super.connect();
            this.dsl = DSL.using(super.getPool(), SQLDialect.MARIADB);
            this.accountsDao = new AccountsDao(dsl.configuration());
            this.ranksDao = new RanksDao(dsl.configuration());
            this.serversDao = new ServersDao(dsl.configuration());
            this.charactersDao = new CharactersDao(dsl.configuration());
            log.info("Successfully connected to auth database.");
        } catch (Exception ex) {
            log.error("A fatal error occured while connecting to the auth database", ex);
            System.exit(-1);
        }
    }

    public Connection getConnection() {
        try {
            return super.getConnection();
        } catch (SQLException ex) {
            log.error("A fatal error occured while fetching a SQL connection from the auth pool", ex);
            System.exit(-1);
            return null;
        }
    }
}
