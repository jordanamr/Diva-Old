package fr.aquazus.diva.protocol.auth.client;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthSearchMessage extends ProtocolMessage {

    private String nickname;

    @Override
    public AuthSearchMessage deserialize(String data) {
        try {
            nickname = data.substring(2);
        } catch (Exception ex) {
            return null;
        }
        return this;
    }
}
