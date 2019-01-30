package fr.aquazus.diva.common.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class DivaRedis {

    private String ip;
    private int port;
    private int maxConnections;
    private JedisPoolConfig poolConfig;
    private JedisPool pool;

    protected DivaRedis(String ip, int port, int maxConnections) {
        this.ip = ip;
        this.port = port;
        this.maxConnections = maxConnections;
    }

    protected void connect() {
        this.poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMaxIdle(maxConnections);
        poolConfig.setMaxTotal(maxConnections);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMinIdle(maxConnections / 4);
        poolConfig.setMaxWaitMillis(2000);
        this.pool = new JedisPool(poolConfig, ip, port);

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
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.publish("diva", message);
            } catch (Exception ex) {
                log.error("An error occurred while publishing", ex);
            }
        });
    }

    protected void onReady() {
        throw new UnsupportedOperationException();
    }

    protected boolean onMessageReceived(String message) {
        throw new UnsupportedOperationException();
    }
}
