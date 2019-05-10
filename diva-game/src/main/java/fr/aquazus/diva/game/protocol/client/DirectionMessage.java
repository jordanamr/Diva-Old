package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

public @Data class DirectionMessage extends ProtocolMessage {

    private int directionId;

    @Override
    public DirectionMessage deserialize(String data) {
        if (data.length() < 3) return null;
        try {
            directionId = Integer.parseInt(data.substring(2));
        } catch (NumberFormatException ex) {
            return null;
        }
        if (directionId < 0 || directionId > 7) return null;
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        client.getCharacter().changeDirection(directionId);
        return true;
    }
}
