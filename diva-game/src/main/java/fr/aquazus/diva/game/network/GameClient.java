package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.game.GameServer;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

@Slf4j
public class GameClient extends DivaClient {

    private final GameServer server;
    private int accountId;

    public GameClient(GameServer server, Client netClient, String ip) {
        super(netClient, ip);
        this.server = server;
        startCommunication();
    }

    @Override
    protected void onReady() {
        sendPacket("HG");
    }

    @Override
    protected void onDisconnect() {
        server.getClients().remove(this);
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() <= 1) return false;
        switch (packet.charAt(0)) {
            case 'A':
                if (packet.length() <= 2) return false;
                switch (packet.charAt(1)) {
                    case 'T':
                        if (packet.length() != 13) {
                            disconnect("Bad ticket format", packet);
                            return true;
                        }
                        String ticketData = ip + "|" + packet.substring(2);
                        if (!server.getTicketsCache().containsKey(ticketData)) {
                            disconnect("Invalid ticket", packet.substring(2));
                            return true;
                        }
                        this.accountId = server.getTicketsCache().get(ticketData);
                        server.getTicketsCache().remove(ticketData);
                        log("Login successful");
                }
        }
        return true;
    }
}
