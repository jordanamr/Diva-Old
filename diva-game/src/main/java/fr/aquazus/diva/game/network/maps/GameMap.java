package fr.aquazus.diva.game.network.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.aquazus.diva.game.network.player.Character;
import fr.aquazus.diva.game.protocol.common.GameActionMessage;
import fr.aquazus.diva.game.protocol.server.GameMovementMessage;
import lombok.Getter;

public class GameMap {

    @Getter
    private int id;
    @Getter
    private String date, key;

    @Getter
    private List<Character> charactersOnMap;

    public GameMap(int id, String date, String key) {
        this.id = id;
        this.date = date;
        this.key = key;
        this.charactersOnMap = Collections.synchronizedList(new ArrayList<>());
    }

    public void addCharacter(Character character) {
        charactersOnMap.add(character);
        for (Character characters : charactersOnMap) {
            if (characters.getId() == character.getId()) continue;
            characters.getClient().sendProtocolMessage(new GameMovementMessage(this, GameMovementMessage.Action.ADD, character));
        }
    }

    public void removeCharacter(Character character) {
        charactersOnMap.remove(character);
        for (Character characters : charactersOnMap) {
            characters.getClient().sendProtocolMessage(new GameMovementMessage(this, GameMovementMessage.Action.REMOVE, character));
        }
    }

    public void moveCharacter(Character character, String path) {
        for (Character characters : charactersOnMap) {
            characters.getClient().sendProtocolMessage(new GameActionMessage(GameActionMessage.Action.MOVEMENT, character.getId() + ";" + path));
        }
    }

    public void sendMessage(Character character, String message) {
        for (Character characters : charactersOnMap) {
            characters.getClient().sendPacket("cMK|" + character.getId() + "|" + character.getName() + "|" + message + "|");
        }
    }

    public void sendSmiley(int spriteId, int id) {
        for (Character characters : charactersOnMap) {
            characters.getClient().sendPacket("cS" + spriteId + "|" + id);
        }
    }

}
