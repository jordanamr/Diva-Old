package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AccountLoginRightsMessage extends ProtocolMessage {

    private boolean hasConsole;

    public AccountLoginRightsMessage(boolean hasConsole) {
        this.hasConsole = hasConsole;
    }

    @Override
    public String serialize() {
        return "AlK" + (hasConsole ? "1" : "0");
    }
}
