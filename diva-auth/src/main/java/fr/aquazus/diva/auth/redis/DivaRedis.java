package fr.aquazus.diva.auth.redis;

import fr.aquazus.diva.auth.AuthServer;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public class DivaRedis implements Runnable {

    private final AuthServer server;
    private final JedisPoolConfig poolConfig;
    private final JedisPool pool;

    public DivaRedis(AuthServer server, String ip, int port) {
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
                }
                @Override
                public void onMessage(String channel, String message) {
                    log.debug("[Redis] <-- " + message);
                    handleMessage(message);
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

    private void handleMessage(String message) {

    }
}
