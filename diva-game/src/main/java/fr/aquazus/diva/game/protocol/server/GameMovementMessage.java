package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.maps.GameMap;
import fr.aquazus.diva.game.network.player.Character;

import java.util.*;

public class GameMovementMessage extends ProtocolMessage {

    private Object context;
    private boolean inFight;
    private List<String> actions = new ArrayList<>();

    public GameMovementMessage(Object context, Action action, Character... characters) {
        this.context = context;
        if (context instanceof GameMap) inFight = false;

        for (Character character : characters) {
            if (action == Action.REMOVE) {
                actions.add("-" + character.getId());
            } else {
                String data = "" + action.value + character.getCellId() + ";" + 1 + ";" + 0 + ";" + character.getId() + ";" + character.getName() + ";" + character.getBreed()
                        + ";" + character.getGfxId() + "^" + 100 + ";" + character.getGender() + ";";
                if (inFight) {
                    data += character.getLevel() + ";"; //TODO
                } else {
                    data += character.getAlignId() + "," + character.getAlignLevel() + "," + character.getAlignRank() + "," +
                            (character.getId() + character.getLevel()) + (character.getAlignId() != 0 ? (character.getAlignDishonor() >= 1 ? ",1" : ",0") : "")
                            + ";" + character.getColor1() + ";" + character.getColor2() + ";" + character.getColor3() + ";" + ",,,," + ";" + 0 + ";;;;;0;;";
                }
                actions.add(data);
            }
        }
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("GM|");
        Iterator<String> iterator = actions.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    public enum Action {
        ADD('+'),
        UPDATE('~'),
        REMOVE('-');

        private final char value;

        Action(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        public static Action valueOf(char value) {
            Optional<Action> key = Arrays.stream(values())
                    .filter(state -> state.value == value)
                    .findFirst();
            return key.orElse(null);
        }
    }

    public enum Type {
        CREATURE(-1),
        MONSTER(-2),
        MONSTER_GROUP(-3),
        NPC(-4),
        MERCHANT(-5),
        COLLECTOR(-6),
        MUTANT(-7),
        PLAYER_MUTANT(-8),
        PARKED_MOUNT(-9),
        PRISM(-10);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type valueOf(int value) {
            Optional<Type> key = Arrays.stream(values())
                    .filter(state -> state.value == value)
                    .findFirst();
            return key.orElse(null);
        }
    }
}
