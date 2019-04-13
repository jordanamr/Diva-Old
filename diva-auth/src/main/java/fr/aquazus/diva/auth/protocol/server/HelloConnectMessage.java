package fr.aquazus.diva.auth.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Random;

public @Data class HelloConnectMessage extends ProtocolMessage {

    private String key;

    public HelloConnectMessage() {
        this.key = generateKey();
    }

    @Override
    public String serialize() {
        return "HC" + key;
    }

    private String generateKey() {
        String keyChars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();
        while (keyBuilder.length() < 32) {
            int index = (int) (random.nextFloat() * keyChars.length());
            keyBuilder.append(keyChars.charAt(index));
        }
        return keyBuilder.toString();
    }
}
