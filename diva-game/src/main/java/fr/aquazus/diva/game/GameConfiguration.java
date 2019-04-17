package fr.aquazus.diva.game;

import fr.aquazus.diva.common.DivaConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public @Data class GameConfiguration extends DivaConfiguration {

    protected String bindIp;
    protected int bindPort;

    protected String redisIp;
    protected int redisPort;
    protected int redisMaxConnections;

    protected String authDatabaseIp;
    protected int authDatabasePort;
    protected String authDatabaseUsername;
    protected String authDatabasePassword;
    protected String authDatabaseName;
    protected int authDatabasePool;

    protected String gameDatabaseIp;
    protected int gameDatabasePort;
    protected String gameDatabaseUsername;
    protected String gameDatabasePassword;
    protected String gameDatabaseName;
    protected int gameDatabasePool;

    protected int serverId;

    GameConfiguration(String fileName) {
        super(fileName);
    }

    @Override
    public void read() throws IOException, NumberFormatException {
        log.info("Reading " + super.getFileName() + "...");
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(super.getFileName())) {
            properties.load(fileInputStream);
            debug = Boolean.parseBoolean(properties.getProperty("debug"));
            this.bindIp = properties.getProperty("bind.ip");
            this.bindPort = Integer.parseInt(properties.getProperty("bind.port"));
            this.redisIp = properties.getProperty("redis.ip");
            this.redisPort = Integer.parseInt(properties.getProperty("redis.port"));
            this.redisMaxConnections = Integer.parseInt(properties.getProperty("redis.maxConnections"));
            this.authDatabaseIp = properties.getProperty("database.auth.ip");
            this.authDatabasePort = Integer.parseInt(properties.getProperty("database.auth.port"));
            this.authDatabaseUsername = properties.getProperty("database.auth.username");
            this.authDatabasePassword = properties.getProperty("database.auth.password");
            this.authDatabaseName = properties.getProperty("database.auth.name");
            this.authDatabasePool = Integer.parseInt(properties.getProperty("database.auth.pool"));
            this.gameDatabaseIp = properties.getProperty("database.game.ip");
            this.gameDatabasePort = Integer.parseInt(properties.getProperty("database.game.port"));
            this.gameDatabaseUsername = properties.getProperty("database.game.username");
            this.gameDatabasePassword = properties.getProperty("database.game.password");
            this.gameDatabaseName = properties.getProperty("database.game.name");
            this.gameDatabasePool = Integer.parseInt(properties.getProperty("database.game.pool"));
            this.serverId = Integer.parseInt(properties.getProperty("server.id"));
        }
    }
}
