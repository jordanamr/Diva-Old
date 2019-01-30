package fr.aquazus.diva.game.redis;

import fr.aquazus.diva.common.redis.DivaRedis;
import fr.aquazus.diva.game.GameServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameRedis extends DivaRedis implements Runnable {

    private final GameServer server;

    public GameRedis(GameServer server, String ip, int port, int maxConnections) {
        super(ip, port, maxConnections);
        this.server = server;
    }

    @Override
    public void run() {
        connect();
    }

    @Override
    protected void onReady() {
        log.debug("Sending Game Hello packet...");
        sendHello();
    }

    @Override
    protected boolean onMessageReceived(String message) {
        if (message.length() < 2) return false;
        switch (message.charAt(0)) {
            case 'A':
                switch (message.charAt(1)) {
                    case 'H':
                        log.debug("Auth said hello");
                        sendHello();
                        return true;
                    case 'T':
                        try {
                            String[] ticketData = message.substring(2).split("\\|");
                            if (server.getId() != Integer.parseInt(ticketData[0])) return true;
                            int ticketAccount = Integer.parseInt(ticketData[1]);
                            server.getTicketsCache().put(ticketData[2] + "|" + ticketData[3], ticketAccount);
                            log.debug("Ticket set for account id " + ticketAccount);
                            return true;
                        } catch (Exception ex) {
                            return false;
                        }
                    default:
                        return false;
                }
            case 'G':
                return true;
            default:
                return false;
        }
    }

    private void sendHello() {
        publish("GH" + server.getId()
                + "|" + server.getConfig().getBindIp() + ":" + server.getConfig().getBindPort()
                + "|" + server.getState().getValue());
    }
}
