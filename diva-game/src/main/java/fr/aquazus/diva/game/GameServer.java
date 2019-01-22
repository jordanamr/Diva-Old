package fr.aquazus.diva.game;

import fr.aquazus.diva.common.DivaServer;
import fr.aquazus.diva.game.database.AuthDatabase;
import fr.aquazus.diva.game.database.GameDatabase;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.redis.GameRedis;
import fr.aquazus.diva.protocol.auth.server.AuthServersMessage;
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
        GameServer.getInstance().start();
    }

    @Getter
    private GameConfiguration config;
    @Getter
    private int id;
    @Getter
    private AuthServersMessage.ServerState state;
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

    private GameServer() {
        state = AuthServersMessage.ServerState.OFFLINE;
        config = new GameConfiguration();
        ticketsCache = Collections.synchronizedMap(new HashMap<>());
        clients = Collections.synchronizedList(new ArrayList<>());
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
        super.listen(config.getBindIp(), config.getBindPort());
        state = AuthServersMessage.ServerState.ONLINE;
        log.info("Starting Redis communication..."); //Note, démarrer redis en tout dernier une fois le serveur prêt.
        redis = new GameRedis(this, config.getRedisIp(), config.getRedisPort());
        new Thread(redis).start();
    }

    @Override
    protected void onClientConnect(Client netClient, String clientIp) {
        this.clients.add(new GameClient(this, netClient, clientIp));
    }
}
