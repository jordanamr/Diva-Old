package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.player.Character;

public class RestrictionsMessage extends ProtocolMessage {

    private Character character;

    public RestrictionsMessage(Character character) {
        this.character = character;
    }

    @Override
    public String serialize() {
        return "AR" + Integer.toString(character.getRestrictions().getValue(), 36);
    }
}
