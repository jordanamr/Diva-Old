package fr.aquazus.diva.auth.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public @Data class AuthQuestionMessage extends ProtocolMessage {

    private String secretQuestion;

    public AuthQuestionMessage(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    @Override
    public String serialize() {
        return "AQ" + URLEncoder.encode(secretQuestion, StandardCharsets.UTF_8);
    }
}
