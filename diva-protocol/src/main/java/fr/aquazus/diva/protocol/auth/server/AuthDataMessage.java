package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public @Data class AuthDataMessage extends ProtocolMessage {

    private int subscriptionTime;
    private HashMap<Integer, Integer> characterList;

    public AuthDataMessage(int subscriptionTime, HashMap<Integer, Integer> characterList) {
        this.subscriptionTime = subscriptionTime;
        this.characterList = characterList;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("AxK");
        builder.append(subscriptionTime);
        for (Map.Entry<Integer, Integer> entry : characterList.entrySet()) {
            builder.append("|");
            builder.append(entry.getKey());
            builder.append(",");
            builder.append(entry.getValue());
        }
        return builder.toString();
    }
}
