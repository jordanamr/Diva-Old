package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data
class AccountLoginCommunityMessage extends ProtocolMessage {

    private Community community;

    public AccountLoginCommunityMessage(Community community) {
        this.community = community;
    }

    @Override
    public String serialize() {
        return "Ac" + community.getValue();
    }

    public enum Community {
        FRENCH(0),
        BRITISH(1),
        INTERNATIONAL(2),
        GERMAN(3),
        SPANISH(4),
        RUSSIAN(5),
        BRAZILIAN(6),
        DUTCH(7),
        ITALIAN(9),
        JAPANESE(10),
        DEBUG(99);

        private final int value;

        Community(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
