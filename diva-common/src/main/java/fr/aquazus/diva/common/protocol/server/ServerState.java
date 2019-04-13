package fr.aquazus.diva.common.protocol.server;

import java.util.Arrays;
import java.util.Optional;

public enum ServerState {
    OFFLINE(0),
    ONLINE(1),
    SAVING(2);

    private final int value;

    ServerState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ServerState valueOf(int value) {
        Optional<ServerState> key = Arrays.stream(values())
                .filter(state -> state.value == value)
                .findFirst();
        return key.orElse(null);
    }
}
