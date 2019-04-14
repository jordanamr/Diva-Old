package fr.aquazus.diva.game;

import fr.aquazus.diva.common.DivaServer;
import fr.aquazus.diva.common.logging.UncaughtExceptionLogger;
import fr.aquazus.diva.common.protocol.server.ServerState;
import fr.aquazus.diva.database.auth.AuthDatabase;
import fr.aquazus.diva.database.game.GameDatabase;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.network.maps.MapsManager;
import fr.aquazus.diva.game.redis.GameRedis;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

import java.util.*;

@Slf4j
public class GameServer extends DivaServer {
    private static GameServer instance = null;

    private static GameServer getInstance() {
        if (instance == null) instance = new GameServer();
        return instance;
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionLogger());
        GameServer.getInstance().start();
    }

    @Getter
    private GameConfiguration config;
    @Getter
    private int id;
    @Getter
    private ServerState state;
    @Getter
    private Map<String, Integer> ticketsCache;
    @Getter
    private final List<GameClient> clients;
    @Getter
    private AuthDatabase authDatabase;
    @Getter
    private GameDatabase gameDatabase;
    @Getter
    private GameRedis redis;
    @Getter
    private MapsManager mapsManager;

    private GameServer() {
        state = ServerState.OFFLINE;
        config = new GameConfiguration();
        ticketsCache = Collections.synchronizedMap(new HashMap<>());
        clients = Collections.synchronizedList(new ArrayList<>());
        mapsManager = new MapsManager(this);
    }

    private void start() {
        try {
            config.read();
            id = config.getServerId();
        } catch (Exception ex) {
            log.error("An error occurred while reading the configuration file. Aborting startup.", ex);
            System.exit(-1);
        }
        log.info("Connecting to auth database...");
        authDatabase = new AuthDatabase(config.getAuthDatabaseIp() + ":" + config.getAuthDatabasePort(), config.getAuthDatabaseUsername(),
                config.getAuthDatabasePassword(), config.getAuthDatabaseName(), config.getAuthDatabasePool());
        authDatabase.connect();
        log.info("Connecting to game database...");
        gameDatabase = new GameDatabase(config.getGameDatabaseIp() + ":" + config.getGameDatabasePort(), config.getGameDatabaseUsername(),
                config.getGameDatabasePassword(), config.getGameDatabaseName(), config.getGameDatabasePool());
        gameDatabase.connect();
        log.info("Loading game data...");
        gameDatabase.load();
        super.listen(config.getBindIp(), config.getBindPort());
        state = ServerState.ONLINE;
        log.info("Starting Redis communication..."); //Note, démarrer redis en tout dernier une fois le serveur prêt.
        redis = new GameRedis(this, config.getRedisIp(), config.getRedisPort(), config.getRedisMaxConnections());
        new Thread(redis).start();
    }

    @Override
    protected void onClientConnect(Client netClient, String clientIp) {
        this.clients.add(new GameClient(this, netClient, clientIp));
    }

    public boolean isAccountOnline(int id) {
        boolean result = false;
        for (GameClient client : clients) {
            if (client.getAccountId() == id) {
                result = true;
                break;
            }
        }
        return result;
    }
}
