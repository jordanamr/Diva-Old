package fr.aquazus.diva.auth.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthQueueMessage extends ProtocolMessage {

    private int position;
    private int p2pQueue;
    private int f2pQueue;
    private boolean subscriber;
    private int queueId;

    public AuthQueueMessage() {
        this.position = 0;
        this.p2pQueue = 0;
        this.f2pQueue = 0;
        this.subscriber = true;
        this.queueId = -1;
    }

    @Override
    public String serialize() {
        return "Af" + position + "|" +
                p2pQueue + "|" +
                f2pQueue + "|" +
                (subscriber ? "1" : "0") + "|" +
                queueId;
    }
}
