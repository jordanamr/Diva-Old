package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

public @Data class FriendsListToggleMessage extends ProtocolMessage {

    private boolean newState;

    @Override
    public FriendsListToggleMessage deserialize(String data) {
        if (data.length() != 1) return null;
        switch (data.charAt(0)) {
            case '+':
                newState = true;
                break;
            case '-':
                newState = false;
                break;
            default:
                return null;
        }
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;
        client.setNotificationsFriends(newState);
        client.sendPacket("BN");
        return true;
    }
}
