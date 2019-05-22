package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;

import java.time.LocalDate;

public class TimeMessage extends ProtocolMessage {

    private long timestamp;

    public TimeMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String serialize() {
        return "BT" + timestamp;
    }
}
