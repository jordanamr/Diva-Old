package fr.aquazus.diva.protocol.game.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class CharacterCreationErrorMessage extends ProtocolMessage {

    private Type type;

    public CharacterCreationErrorMessage(Type type) {
        this.type = type;
    }

    @Override
    public String serialize() {
        return "AAE" + type.getValue();
    }

    public enum Type {
        NO_EMPTY_SLOT('f'),
        NAME_INVALID('n'),
        NAME_TAKEN('a'),
        DATA_INVALID('F');

        private final char value;

        Type(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }
}
