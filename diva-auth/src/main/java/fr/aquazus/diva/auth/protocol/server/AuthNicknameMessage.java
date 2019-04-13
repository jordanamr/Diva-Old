package fr.aquazus.diva.auth.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthNicknameMessage extends ProtocolMessage {

    private String nickname;

    public AuthNicknameMessage(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String serialize() {
        return "Ad" + nickname;
    }
}
