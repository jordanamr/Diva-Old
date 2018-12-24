package fr.aquazus.diva.protocol.auth.client;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public @Data class AccountLoginSearchMessage extends ProtocolMessage {

    private String nickname;

    @Override
    public AccountLoginSearchMessage deserialize(String data) {
        nickname = data.substring(2);
        return this;
    }
}
