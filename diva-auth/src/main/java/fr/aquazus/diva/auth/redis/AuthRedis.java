package fr.aquazus.diva.auth.redis;

import fr.aquazus.diva.auth.AuthServer;
import fr.aquazus.diva.common.redis.DivaRedis;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthRedis extends DivaRedis implements Runnable {

    private final AuthServer server;

    public AuthRedis(AuthServer server, String ip, int port) {
        super(ip, port);
        this.server = server;
    }

    @Override
    public void run() {
        connect();
    }

    @Override
    protected void onReady() {
        log.debug("Sending Auth Hello packet...");
        publish("AH");
    }

    @Override
    protected boolean onMessageReceived(String message) {
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

    public void setTicket(int serverId, String ticket, String ip) {
        publish("AT" + serverId + "|" + ticket + "|" + ip);
    }
}
