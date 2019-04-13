package fr.aquazus.diva.auth.protocol.client;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
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
