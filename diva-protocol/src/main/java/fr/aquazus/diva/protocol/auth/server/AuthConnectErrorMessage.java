package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthConnectErrorMessage extends ProtocolMessage {

    private Type type;
    private String extraData;

    public AuthConnectErrorMessage(Type type) {
        this(type, "");
    }

    public AuthConnectErrorMessage(Type type, String extraData) {
        this.type = type;
        this.extraData = extraData;
    }

    @Override
    public String serialize() {
        return "AXE" + type.getValue() + extraData;
    }

    public enum Type {
        NOT_AVAILABLE('d'),
        RESTRICTED('r'), //TODO Implement
        F2P_FULL('f'), //TODO Implement
        FULL('F'); //TODO Implement

        private final char value;

        Type(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }
}
