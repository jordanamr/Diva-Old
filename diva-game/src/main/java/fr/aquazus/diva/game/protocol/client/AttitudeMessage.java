package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

public @Data class AttitudeMessage extends ProtocolMessage {

    private int attitudeId;

    @Override
    public AttitudeMessage deserialize(String data) {
        if (data.length() < 3) return null;
        try {
            attitudeId = Integer.parseInt(data.substring(2));
        } catch (NumberFormatException ex) {
            return null;
        }
        if (attitudeId < 1 || attitudeId > 18) return null;
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        if (!client.getCharacter().getAttitudes().contains(attitudeId)) {
            client.sendPacket("eUE");
            return true;
        }
        client.getCharacter().useAttitude(attitudeId);
        return true;
    }
}
