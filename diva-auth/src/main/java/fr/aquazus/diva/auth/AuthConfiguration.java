package fr.aquazus.diva.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public @Data class AuthConfiguration {

    public static boolean debug;
    protected String bindIp;
    protected int bindPort;

    protected String redisIp;
    protected int redisPort;

    protected String databaseIp;
    protected int databasePort;
    protected String databaseUsername;
    protected String databasePassword;
    protected String databaseName;
    protected int databasePool;

    void read() throws IOException, NumberFormatException {
        log.info("Reading auth.properties...");
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("auth.properties")) {
            properties.load(fileInputStream);
            debug = Boolean.parseBoolean(properties.getProperty("debug"));
            this.bindIp = properties.getProperty("bind.ip");
            this.bindPort = Integer.parseInt(properties.getProperty("bind.port"));
            this.redisIp = properties.getProperty("redis.ip");
            this.redisPort = Integer.parseInt(properties.getProperty("redis.port"));
            this.databaseIp = properties.getProperty("database.ip");
            this.databasePort = Integer.parseInt(properties.getProperty("database.port"));
            this.databaseUsername = properties.getProperty("database.username");
            this.databasePassword = properties.getProperty("database.password");
            this.databaseName = properties.getProperty("database.name");
            this.databasePool = Integer.parseInt(properties.getProperty("database.pool"));
        }
    }
}
