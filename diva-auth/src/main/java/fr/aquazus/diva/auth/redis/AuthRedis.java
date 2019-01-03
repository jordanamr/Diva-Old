package fr.aquazus.diva.auth.redis;

import fr.aquazus.diva.auth.AuthServer;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public class AuthRedis implements Runnable {

    private final AuthServer server;
    private final JedisPoolConfig poolConfig;
    private final JedisPool pool;

    public AuthRedis(AuthServer server, String ip, int port) {
        this.server = server;
        this.poolConfig = new JedisPoolConfig();
        this.pool = new JedisPool(poolConfig, ip, port);
    }

    @Override
    public void run() {
        try {
            pool.getResource().subscribe(new JedisPubSub() {
                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    log.info("Successfully subscribed to Redis!");
                    log.debug("Sending Auth Hello packet...");
                    pool.getResource().publish(channel, "AH");
                }

                @Override
                public void onMessage(String channel, String message) {
                    log.debug("[Redis] <-- " + message);
                    if (!handleMessage(message)) {
                        log.warn("Invalid exchange packet: " + message);
                    }
                }

                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                    log.error("The Redis channel got disconnected.");
                    System.exit(-1);
                }
            }, "diva");
        } catch (Exception ex) {
            log.error("An error occurred while loading Redis", ex);
            System.exit(-1);
        }
    }

    public void setTicket(int serverId, String ticket, String ip) {
        pool.getResource().publish("diva", "AT" + serverId + "|" + ticket + "|" + ip);
    }

    private boolean handleMessage(String message) {
        if (message.length() < 2) return false;
        switch (message.charAt(0)) {
            case 'A':
                return true;
            case 'G':
                switch (message.charAt(1)) {
                    case 'H':
                        /* TODO Game Hello, sends IP & Status
                            Format: GHid|ip|status */
                    case 'S':
                        /* TODO Game Status, update server status
                            Format: GSid|status */
                    case 'K':
                        /* TODO (Future) Game Keepalive, keep a trace of the GameServer
                            activity in case it crashed */
                    default:
                        return false;
                }
            default:
                return false;
        }
    }
}
