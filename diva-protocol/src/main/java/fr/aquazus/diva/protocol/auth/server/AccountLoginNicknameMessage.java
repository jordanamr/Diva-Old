package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AccountLoginNicknameMessage extends ProtocolMessage {

    private String nickname;

    public AccountLoginNicknameMessage(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String serialize() {
        return "Ad" + nickname;
    }
}
