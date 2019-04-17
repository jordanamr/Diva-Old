package fr.aquazus.diva.game.protocol.common;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameCipher;
import lombok.Data;

import java.util.*;

public @Data class GameActionMessage extends ProtocolMessage {

    private Action action;
    private Object actionData;
    private GameCipher cipher = GameCipher.getInstance();

    public GameActionMessage() { }

    public GameActionMessage(Action action, Object actionData) {
        this.action = action;
        this.actionData = actionData;
    }

    @Override
    public String serialize() {
        String data = "GA";
        switch (action) {
            case MOVEMENT:
                data += "0;1;" + actionData;
        }
        return data;
    }

    @Override
    public GameActionMessage deserialize(String data) {
        action = Action.valueOf(Integer.parseInt(data.substring(0, 3)));
        if (action == null) return this;
        switch (action) {
            case MOVEMENT:
                actionData = cipher.extractFullPath(data.substring(3), 5000);
        }
        return this;
    }

    public enum Action {
        MOVEMENT(1);

        private final int value;

        Action(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Action valueOf(int value) {
            Optional<Action> key = Arrays.stream(values())
                    .filter(state -> state.value == value)
                    .findFirst();
            return key.orElse(null);
        }
    }

    public String makeNewPath(int startCell) {
        //TODO Check path
        LinkedHashMap<Integer, Integer> newPath = new LinkedHashMap<>();
        newPath.put(startCell, 0);
        newPath.putAll((LinkedHashMap<Integer, Integer>) actionData);
        return newPath.entrySet().toArray()[newPath.size() - 1] + "|" + cipher.compressFullPath(newPath);
    }
}
