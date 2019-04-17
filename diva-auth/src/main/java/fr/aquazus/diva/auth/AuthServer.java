package fr.aquazus.diva.auth;

import fr.aquazus.diva.auth.network.AuthCipher;
import fr.aquazus.diva.auth.network.AuthClient;
import fr.aquazus.diva.auth.redis.AuthRedis;
import fr.aquazus.diva.common.DivaServer;
import fr.aquazus.diva.common.logging.UncaughtExceptionLogger;
import fr.aquazus.diva.database.auth.AuthDatabase;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Servers;
import fr.aquazus.diva.auth.protocol.server.AuthServersMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

import java.util.*;

@Slf4j
public class AuthServer extends DivaServer {

    private static AuthServer instance = null;

    private static AuthServer getInstance() {
        if (instance == null) instance = new AuthServer();
        return instance;
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionLogger());
        AuthServer.getInstance().start();
    }

    private AuthConfiguration config;
    @Getter
    private final List<AuthClient> clients;
    @Getter
    private final AuthCipher cipher;
    @Getter
    private AuthDatabase database;
    @Getter
    private AuthRedis redis;
    @Getter
    private Map<Integer, AuthServersMessage.Server> serversCache;
    @Getter
    private Map<Integer, String> serversIpCache;

    private AuthServer() {
        config = new AuthConfiguration("auth.properties");
        clients = Collections.synchronizedList(new ArrayList<>());
        cipher = new AuthCipher();
        serversCache = Collections.synchronizedMap(new HashMap<>());
        serversIpCache = Collections.synchronizedMap(new HashMap<>());
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
        log.info("Initializing GameServers list...");
        for (Servers servers : database.getServersDao().findAll()) {
            serversCache.put(servers.getId(), new AuthServersMessage.Server(servers.getId(), AuthServersMessage.ServerState.OFFLINE,
                    servers.getCompletion(), servers.getP2p().intValue() == 1));
        }
        log.info(serversCache.size() + " GameServers detected.");
        log.info("Starting Redis communication...");
        redis = new AuthRedis(this, config.getRedisIp(), config.getRedisPort(), config.getRedisMaxConnections());
        new Thread(redis).start();
        super.listen(config.getBindIp(), config.getBindPort());
    }

    @Override
    protected void onClientConnect(Client netClient, String clientIp) {
        this.clients.add(new AuthClient(this, netClient, clientIp));
    }

    public void resendServersData() {
        log.debug("Resending servers data...");
        for (AuthClient client : clients) {
            if (client.getState() == AuthClient.State.SELECT_SERVER) client.updateServersData();
        }
    }

    public boolean isAccountOnline(int id) {
        boolean result = false;
        for (AuthClient client : clients) {
            if (client.getAccountId() == id) {
                result = true;
                break;
            }
        }
        return result;
    }
}
