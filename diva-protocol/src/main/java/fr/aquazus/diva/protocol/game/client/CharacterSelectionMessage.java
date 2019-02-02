package fr.aquazus.diva.protocol.game.client;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class CharacterSelectionMessage extends ProtocolMessage {

    private int characterId;

    @Override
    public CharacterSelectionMessage deserialize(String data) {
        if (data.length() < 3) return null;
        try {
            this.characterId = Integer.parseInt(data.substring(2));
        } catch (NumberFormatException ex) {
            return null;
        }
        return this;
    }
}
