package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;


public @Data class GameActionCancelMessage extends ProtocolMessage {

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        if (packet.length() < 7 || !packet.contains("\\|")) return false;
        GameClient client = (GameClient) netClient;
        String[] extraData = packet.substring(3).split("\\|");
        int actionId = Integer.parseInt(extraData[0]);
        switch (actionId) {
            case 0:
                client.getCharacter().setCellId(Integer.parseInt(extraData[1]));
                client.sendPacket("BN");
                return true;
            default:
                return false;
        }
    }
}
