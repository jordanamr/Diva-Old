package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.Optional;

public @Data
class AuthCommunityMessage extends ProtocolMessage {

    private Community community;

    public AuthCommunityMessage(Community community) {
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

        public static Community valueOf(int value) {
            Optional<Community> key = Arrays.stream(values())
                    .filter(population -> population.value == value)
                    .findFirst();
            return key.orElse(null);
        }
    }
}
