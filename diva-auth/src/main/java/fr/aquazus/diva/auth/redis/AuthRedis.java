package fr.aquazus.diva.auth.redis;

import fr.aquazus.diva.auth.AuthServer;
import fr.aquazus.diva.common.redis.DivaRedis;
import fr.aquazus.diva.protocol.auth.server.AuthServersMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthRedis extends DivaRedis implements Runnable {

    private final AuthServer server;

    public AuthRedis(AuthServer server, String ip, int port, int maxConnections) {
        super(ip, port, maxConnections);
        this.server = server;
    }

    @Override
    public void run() {
        connect();
    }

    @Override
    protected void onReady() {
        log.info("Requesting GameServers availability...");
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
                        try {
                            String[] helloData = message.substring(2).split("\\|");
                            int helloId = Integer.parseInt(helloData[0]);
                            String helloIp = helloData[1];
                            int helloStatus = Integer.parseInt(helloData[2]);
                            server.getServersCache().get(helloId).setState(AuthServersMessage.ServerState.valueOf(helloStatus));
                            server.getServersIpCache().put(helloId, helloIp);
                            log.info("GameServer " + helloId + " said hello with status " + helloStatus);
                            server.resendServersData();
                            return true;
                        } catch (Exception ex) {
                            return false;
                        }
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

    public void setTicket(int serverId, int accountId, String ip, String ticket) {
        publish("AT" + serverId + "|" + accountId + "|" + ip + "|" + ticket);
    }
}
