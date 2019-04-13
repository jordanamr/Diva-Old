package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

public @Data class CharacterDeletionMessage extends ProtocolMessage {

    private int characterId;
    private String secretAnswer;

    @Override
    public CharacterDeletionMessage deserialize(String data) {
        if (data.isBlank() || data.substring(2).isBlank()) return null;
        String[] extraData = data.substring(2).split("\\|");
        try {
            this.characterId = Integer.parseInt(extraData[0]);
        } catch (NumberFormatException ex) {
            return null;
        }
        if (extraData.length < 2) {
            this.secretAnswer = "";
        } else {
            this.secretAnswer = extraData[1];
        }
        return this;
    }
}
