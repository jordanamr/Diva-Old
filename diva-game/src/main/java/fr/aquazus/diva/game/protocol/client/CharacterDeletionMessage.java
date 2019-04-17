package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.protocol.server.CharacterListMessage;
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

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        Characters characterToDelete = client.getServer().getAuthDatabase().getCharactersDao().fetchOneById(characterId);
        if (characterToDelete == null) {
            client.disconnect("Trying to delete an unknown character", "" + characterId);
            return true;
        }
        if (characterToDelete.getAccountId() != client.getAccountId()) {
            client.disconnect("Trying to delete someone else's character", "" + characterId);
            return true;
        }
        if (characterToDelete.getLevel() >= 20 && !client.getSecretAnswer().equalsIgnoreCase(secretAnswer)) {
            client.sendPacket("ADE");
            return true;
        }
        client.getServer().getAuthDatabase().getCharactersDao().delete(characterToDelete);
        client.setCharacterCount(client.getCharacterCount() - 1);
        client.sendPacket("BN");
        client.sendProtocolMessage(new CharacterListMessage(client.getRemainingSubscription(), client.getCharacterCount(),
                client.getServer().getAuthDatabase().getCharactersDao().fetchByAccountId(client.getAccountId())));
        return true;
    }
}
