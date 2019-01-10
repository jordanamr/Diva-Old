package fr.aquazus.diva.common.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public abstract class DivaRedis {

    private final JedisPoolConfig poolConfig;
    private final JedisPool pool;

    protected DivaRedis(String ip, int port) {
        this.poolConfig = new JedisPoolConfig();
        this.pool = new JedisPool(poolConfig, ip, port);
    }

    protected void connect() {
        try {
            pool.getResource().subscribe(new JedisPubSub() {
                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    log.info("Successfully subscribed to Redis!");
                    onReady();
                }

                @Override
                public void onMessage(String channel, String message) {
                    log.debug("[Redis] <-- " + message);
                    if (!onMessageReceived(message)) {
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

    protected void publish(String message) {
        log.debug("[Redis] --> " + message);
        pool.getResource().publish("diva", message);
    }

    protected void onReady() {
        throw new UnsupportedOperationException();
    }

    protected boolean onMessageReceived(String message) {
        throw new UnsupportedOperationException();
    }
}
