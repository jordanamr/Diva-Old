package fr.aquazus.diva.auth.protocol.client;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
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
