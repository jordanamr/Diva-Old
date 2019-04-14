package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.player.Character;

public class CharacterSelectedMessage extends ProtocolMessage {

    private Character character;

    public CharacterSelectedMessage(Character character) {
        this.character = character;
    }

    @Override
    public String serialize() {
        //ASK|2752795|Aquazus-Arc|22|9|0|90|c75cc|5345a|53054|items
        StringBuilder builder = new StringBuilder("ASK|");
        builder.append(character.getId()).append('|');
        builder.append(character.getName()).append('|');
        builder.append(character.getLevel()).append('|');
        builder.append(character.getBreed()).append('|');
        builder.append(character.getGender()).append('|');
        builder.append(character.getGfxId()).append('|');
        builder.append(character.getColor1()).append('|');
        builder.append(character.getColor2()).append('|');
        builder.append(character.getColor3()).append('|');
        //TODO Inventory items
        return builder.toString();
    }
}
