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
        charactersOnMap.forEach(c -> {
            if (c.getId() == character.getId()) return;
            c.getClient().sendProtocolMessage(new GameMovementMessage(this, GameMovementMessage.Action.ADD, character));
        });
    }

    public void removeCharacter(Character character) {
        charactersOnMap.remove(character);
        charactersOnMap.forEach(c -> c.getClient().sendProtocolMessage(new GameMovementMessage(this, GameMovementMessage.Action.REMOVE, character)));
    }

    public void moveCharacter(Character character, String path) {
        charactersOnMap.forEach(c -> c.getClient().sendProtocolMessage(new GameActionMessage(GameActionMessage.Action.MOVEMENT, character.getId() + ";" + path)));
    }

    public void sendMessage(Character character, String message) {
        charactersOnMap.forEach(c -> c.getClient().sendPacket("cMK|" + character.getId() + "|" + character.getName() + "|" + message + "|"));
    }

    public void sendSmiley(int spriteId, int id) {
        charactersOnMap.forEach(c -> c.getClient().sendPacket("cS" + spriteId + "|" + id));
    }

    public void useAttitude(int spriteId, int id) {
        charactersOnMap.forEach(c -> c.getClient().sendPacket("eUK" + spriteId + "|" + id));
    }

    public void changeDirection(int spriteId, int id) {
        charactersOnMap.forEach(c -> c.getClient().sendPacket("eD" + spriteId + "|" + id));
    }

}
