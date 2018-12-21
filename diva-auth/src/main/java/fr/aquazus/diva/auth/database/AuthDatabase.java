package fr.aquazus.diva.auth.database;

import fr.aquazus.diva.database.DivaDatabase;
import fr.aquazus.diva.database.generated.auth.tables.daos.AccountsDao;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

import static fr.aquazus.diva.database.generated.auth.Tables.*;

@Slf4j
public class AuthDatabase extends DivaDatabase {

    @Getter
    private DSLContext dsl;
    @Getter
    private AccountsDao accountsDao;

    public AuthDatabase(String server, String username, String password, String database, int poolSize) {
        super(server, username, password, database, poolSize);
    }

    public void connect() {
        try {
            super.connect();
            this.dsl = DSL.using(super.getPool(), SQLDialect.MARIADB);
            this.accountsDao = new AccountsDao(dsl.configuration());
        } catch (SQLException ex) {
            log.error("A fatal error occured while connecting to the SQL server", ex);
            System.exit(-1);
        }
    }

    public Connection getConnection() {
        try {
            return super.getConnection();
        } catch (SQLException ex) {
            log.error("A fatal error occured while fetching a SQL connection from the pool", ex);
            System.exit(-1);
            return null;
        }
    }

    public void updateNickname(int accountId, String nickname) {

    }
}
