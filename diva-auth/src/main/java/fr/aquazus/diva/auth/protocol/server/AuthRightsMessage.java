package fr.aquazus.diva.auth.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthRightsMessage extends ProtocolMessage {

    private boolean hasConsole;

    public AuthRightsMessage(boolean hasConsole) {
        this.hasConsole = hasConsole;
    }

    @Override
    public String serialize() {
        return "AlK" + (hasConsole ? "1" : "0");
    }
}
