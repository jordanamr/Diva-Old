package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public @Data class AccountLoginQuestionMessage extends ProtocolMessage {

    private String secretQuestion;

    public AccountLoginQuestionMessage(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    @Override
    public String serialize() {
        return "AQ" + URLEncoder.encode(secretQuestion, StandardCharsets.UTF_8);
    }
}