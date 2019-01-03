package fr.aquazus.diva.protocol.auth.client;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthConnectMessage extends ProtocolMessage {

    private int serverId;

    @Override
    public AuthConnectMessage deserialize(String data) {
        try {
            serverId = Integer.parseInt(data.substring(2));
        } catch (Exception ex) {
            return null;
        }
        return this;
    }
}
