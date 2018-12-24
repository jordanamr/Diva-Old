package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public @Data class AccountLoginSearchResultMessage extends ProtocolMessage {

    private HashMap<Integer, Integer> characterCount;

    public AccountLoginSearchResultMessage() {
        this(new HashMap<>());
    }

    public AccountLoginSearchResultMessage(HashMap<Integer, Integer> characterCount) {
        this.characterCount = characterCount;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("AF");
        Iterator<Map.Entry<Integer, Integer>> iterator = characterCount.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            builder.append(entry.getKey());
            builder.append(",");
            builder.append(entry.getValue());
            if (iterator.hasNext()) {
                builder.append(";");
            }
        }
        return builder.toString();
    }
}
