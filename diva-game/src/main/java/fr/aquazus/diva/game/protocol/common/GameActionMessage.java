package fr.aquazus.diva.game.protocol.common;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

import java.util.*;

public @Data class GameActionMessage extends ProtocolMessage {

    private Action action;
    private Object actionData;

    public GameActionMessage() {

    }

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
                actionData = extractFullPath(data.substring(3), 5000);
                //TODO Check path
        }
        return this;
    }

    public String makeNewPath(int startCell) {
        LinkedHashMap<Integer, Integer> newPath = new LinkedHashMap<>();
        newPath.put(startCell, 0);
        newPath.putAll((LinkedHashMap<Integer, Integer>) actionData);
        return newPath.entrySet().toArray()[newPath.size() - 1] + "|" + compressFullPath(newPath);
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

    private final List<Character> zkArray = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_');

    public LinkedHashMap<Integer, Integer> extractFullPath(String compressedData, int mapSize) {
        LinkedHashMap<Integer, Integer> fullPath = new LinkedHashMap<>();
        char[] data = compressedData.toCharArray();
        for (int i = 0; i < data.length; i = i + 3) {
            int cellId = (decode64((data[i + 1])) & 15) << 6 | decode64(data[i + 2]);
            int direction = decode64(data[i]);
            if (cellId < 0 || cellId > mapSize) return null;
            fullPath.put(cellId, direction);
        }
        return fullPath;
    }

    public String compressFullPath(LinkedHashMap<Integer, Integer> fullPath) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Integer> path : fullPath.entrySet()) {
            builder.append(encode64(path.getValue() & 7));
            builder.append(encode64((path.getKey() & 4032) >> 6));
            builder.append(encode64(path.getKey() & 63));
        }
        return builder.toString();
    }

    private int decode64(char data) {
        return zkArray.indexOf(data);
    }

    private char encode64(int data) {
        return zkArray.get(data);
    }
}
