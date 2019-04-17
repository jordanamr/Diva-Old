package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.common.utils.StringUtils;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.protocol.server.CharacterCreationErrorMessage;
import fr.aquazus.diva.game.protocol.server.CharacterListMessage;
import lombok.Data;

import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;

public @Data class CharacterCreationMessage extends ProtocolMessage {

    private String characterName;
    private byte characterBreed, characterGender;
    private int characterColor1, characterColor2, characterColor3;
    private boolean invalidData;

    @Override
    public CharacterCreationMessage deserialize(String data) {
        if (data.isBlank() || data.substring(2).isBlank() || !data.contains("|") || data.length() <= 12) return null;
        String[] characterData = data.substring(2).split("\\|");
        if (characterData.length != 6) return null;
        characterName = characterData[0];
        try {
            characterBreed = Byte.parseByte(characterData[1]);
            characterGender = Byte.parseByte(characterData[2]);
            characterColor1 = Integer.parseInt(characterData[3]);
            characterColor2 = Integer.parseInt(characterData[4]);
            characterColor3 = Integer.parseInt(characterData[5]);
        } catch (NumberFormatException ex) {
            invalidData = true;
            return this;
        }
        if (characterBreed < 1 || characterBreed > 12) invalidData = true;
        if (characterGender != 0 && characterGender != 1) invalidData = true;
        if (characterColor1 < -1 || characterColor1 > 16777215) invalidData = true;
        if (characterColor2 < -1 || characterColor2 > 16777215) invalidData = true;
        if (characterColor3 < -1 || characterColor3 > 16777215) invalidData = true;
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        if (invalidData) {
            client.sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.DATA_INVALID));
            return true;
        }
        if (client.getCharacterCount() >= 5) {
            client.sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NO_EMPTY_SLOT));
            return true;
        }
        if (!StringUtils.isValidCharacterName(characterName)) {
            client.sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_INVALID));
            return true;
        }
        if (client.getServer().getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.SERVER_ID.eq(client.getServer().getId())).and(CHARACTERS.NAME.eq(characterName)).fetchOne() != null) {
            client.sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_TAKEN));
            return true;
        }
        client.getServer().getAuthDatabase().getDsl().insertInto(CHARACTERS).set(CHARACTERS.ACCOUNT_ID, client.getAccountId())
                .set(CHARACTERS.SERVER_ID, client.getServer().getId()).set(CHARACTERS.NAME, characterName)
                .set(CHARACTERS.BREED, characterBreed).set(CHARACTERS.GENDER, characterGender)
                .set(CHARACTERS.GFX_ID, Short.parseShort(characterBreed + "" + characterGender))
                .set(CHARACTERS.COLOR1, characterColor1).set(CHARACTERS.COLOR2, characterColor2)
                .set(CHARACTERS.COLOR3, characterColor3).set(CHARACTERS.LEVEL, client.getServer().getConfig().getStartLevel())
                .set(CHARACTERS.KAMAS, client.getServer().getConfig().getStartKamas()).set(CHARACTERS.MAP_ID, client.getServer().getConfig().getStartMapId())
                .set(CHARACTERS.CELL_ID, client.getServer().getConfig().getStartMapCell()).execute();
        client.setCharacterCount(client.getCharacterCount() + 1);
        client.sendPacket("BN");
        client.sendPacket("AAK");
        client.sendProtocolMessage(new CharacterListMessage(client.getRemainingSubscription(), client.getCharacterCount(),
                client.getServer().getAuthDatabase().getCharactersDao().fetchByAccountId(client.getAccountId())));
        return true;
    }
}
