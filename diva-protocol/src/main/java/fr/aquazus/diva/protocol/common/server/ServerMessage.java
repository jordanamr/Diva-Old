package fr.aquazus.diva.protocol.common.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.Iterator;

public @Data class ServerMessage extends ProtocolMessage {

    private Type type;
    private int id;
    private String[] args;

    public ServerMessage(Type type, int id, String... args) {
        this.type = type;
        this.id = id;
        this.args = args;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("M");
        builder.append(type.getValue());
        builder.append(id);
        Iterator<String> iterator = Arrays.asList(args).iterator();
        if (iterator.hasNext()) builder.append("|");
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(";");
            }
        }
        return builder.toString();
    }

    public enum Type {
        AUTH(0),
        GAME(1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
