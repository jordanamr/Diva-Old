package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

public @Data class SmileyMessage extends ProtocolMessage {

    private int smileyId;

    @Override
    public SmileyMessage deserialize(String data) {
        if (data.length() < 3) return null;
        try {
            smileyId = Integer.parseInt(data.substring(2));
        } catch (NumberFormatException ex) {
            return null;
        }
        if (smileyId < 1 || smileyId > 15) return null;
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        client.getCharacter().sendSmiley(smileyId);
        return true;
    }
}
