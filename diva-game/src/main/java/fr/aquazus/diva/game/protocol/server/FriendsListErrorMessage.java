package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

public @Data class FriendsListErrorMessage extends ProtocolMessage {

    private Type type;

    public FriendsListErrorMessage(Type type) {
        this.type = type;
    }

    @Override
    public String serialize() {
        return "FAE" + type.getValue();
    }

    public enum Type {
        CANT_ADD_FRIEND_NOT_FOUND('f'),
        CANT_ADD_YOU('y'),
        ALREADY_YOUR_FRIEND('a'),
        FRIENDS_LIST_FULL('m');

        private final char value;

        Type(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }
}
