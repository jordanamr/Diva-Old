package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Random;

public @Data class AccountLoginErrorMessage extends ProtocolMessage {

    private Type type;
    private String extraData;

    public AccountLoginErrorMessage(Type type) {
        this(type, "");
    }

    public AccountLoginErrorMessage(Type type, String extraData) {
        this.type = type;
        this.extraData = extraData;
    }

    @Override
    public String serialize() {
        return "AlE" + type.getValue() + extraData;
    }

    public enum Type {
        CONNECT_NOT_FINISHED('n'), //Won't implement
        ALREADY_LOGGED('a'), //TODO Implement
        ALREADY_LOGGED_GAME_SERVER('c'), //TODO Implement
        BAD_VERSION('v'),
        NOT_PLAYER('p'), //Won't implement
        BANNED('b'), //TODO Implement
        U_DISCONNECT_ACCOUNT('d'), //TODO Implement
        KICKED('k'), //TODO Implement
        SERVER_FULL('w'), //TODO Implement
        OLD_ACCOUNT('o'), //Won't implement
        OLD_ACCOUNT_USE_NEW('e'), //Won't implement
        MAINTAIN_ACCOUNT('m'), //Won't implement
        CHOOSE_NICKNAME('r'), //TODO Implement
        NICKNAME_TAKEN('s'), //TODO Implement
        ACCESS_DENIED_MINICLIP('f'); //Won't implement

        private final char value;

        Type(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }
}
