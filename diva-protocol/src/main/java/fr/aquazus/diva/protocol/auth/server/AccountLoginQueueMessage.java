package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Random;

public @Data class AccountLoginQueueMessage extends ProtocolMessage {

    private int position;
    private int p2pQueue;
    private int f2pQueue;
    private boolean subscriber;
    private int queueId;

    public AccountLoginQueueMessage() {
        this.position = 1;
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
