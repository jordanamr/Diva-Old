package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public @Data class AuthServersMessage extends ProtocolMessage {

    private List<Server> serverList;
    private boolean p2p;

    public AuthServersMessage(List<Server> serverList, boolean p2p) {
        this.serverList = serverList;
        this.p2p = p2p;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("AH");
        Iterator<Server> iterator = serverList.iterator();
        while (iterator.hasNext()) {
            Server server = iterator.next();
            builder.append(server.getId()).append(";");
            builder.append(server.getState().getValue()).append(";");
            builder.append(server.getCompletion()).append(";");
            builder.append(server.isP2p() && !p2p ? "0" : "1");
            if (iterator.hasNext()) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    public static @Data class Server {
        private int id;
        private ServerState state;
        private int completion;
        private boolean p2p;

        public Server(int id, ServerState state, int completion, boolean p2p) {
            this.id = id;
            this.state = state;
            this.completion = completion;
            this.p2p = p2p;
        }
    }

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
}


