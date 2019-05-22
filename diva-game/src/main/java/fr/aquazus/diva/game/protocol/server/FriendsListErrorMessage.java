package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

public @Data class FriendsListErrorMessage extends ProtocolMessage {

    private FriendsListMessage.Type type;
    private Code code;

    public FriendsListErrorMessage(FriendsListMessage.Type type, Code code) {
        this.type = type;
        this.code = code;
    }

    @Override
    public String serialize() {
        return type.getId() + "AE" + code.getValue();
    }

    public enum Code {
        CANT_ADD_FRIEND_NOT_FOUND('f'),
        CANT_ADD_YOU('y'),
        ALREADY_YOUR_FRIEND('a'),
        FRIENDS_LIST_FULL('m');

        private final char value;

        Code(char value) {
            this.value = value;
        }

        public final char getValue() {
            return value;
        }
    }
}
