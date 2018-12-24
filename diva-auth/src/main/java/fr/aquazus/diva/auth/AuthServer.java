package fr.aquazus.diva.auth;

import fr.aquazus.diva.auth.database.AuthDatabase;
import fr.aquazus.diva.auth.network.AuthCipher;
import fr.aquazus.diva.auth.network.AuthClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AuthServer {

    private static AuthServer instance = null;

    private static AuthServer getInstance() {
        if (instance == null) instance = new AuthServer();
        return instance;
    }

    public static void main(String[] args) {
        AuthServer.getInstance().start();
    }

    private AuthConfiguration config;
    private final List<AuthClient> clients;
    @Getter
    private final AuthCipher cipher;
    @Getter
    private AuthDatabase database;
    @Getter
    private String[] forbiddenNames;

    private AuthServer() {
        config = new AuthConfiguration();
        clients = Collections.synchronizedList(new ArrayList<>());
        cipher = new AuthCipher();
        forbiddenNames = new String[] {"xelor", "iop", "feca", "eniripsa", "sadida", "ecaflip", "enutrof", "pandawa", "sram", "cra", "osamodas", "sacrieur", "drop", "mule", "admin", "ankama", "dofus", "staff", "moderateur"};
    }

    private void start() {
        try {
            config.read();
        } catch (Exception ex) {
            log.error("An error occurred while reading the configuration file. Aborting startup.", ex);
            System.exit(-1);
        }
        log.info("Connecting to database...");
        database = new AuthDatabase(config.getDatabaseIp() + ":" + config.getDatabasePort(), config.getDatabaseUsername(),
                config.getDatabasePassword(), config.getDatabaseName(), config.getDatabasePool());
        database.connect();
        log.info("Successfully connected to database.");
        listen();
    }

    private void listen() {
        log.info("Starting net server...");
        Server netServer = new Server(1024);
        netServer.onConnect(netClient -> {
            String clientIp;
            try {
                clientIp = netClient.getChannel().getRemoteAddress().toString().substring(1).split(":")[0];
            } catch (Exception ex) {
                log.error("An error occurred while parsing an user IP", ex);
                if (netClient.getChannel().isOpen()) netClient.close();
                return;
            }
            log.info("[" + clientIp + "] connected!");
            this.clients.add(new AuthClient(this, netClient, clientIp));
        });
        netServer.bind(config.getBindIp(), config.getBindPort());
    }
}
