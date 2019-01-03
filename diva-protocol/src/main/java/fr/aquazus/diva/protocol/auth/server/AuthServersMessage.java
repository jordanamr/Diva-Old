package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public @Data class AuthServersMessage extends ProtocolMessage {

    private List<Server> serverList;

    public AuthServersMessage(List<Server> serverList) {
        this.serverList = serverList;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("AH");
        Iterator<Server> iterator = serverList.iterator();
        while (iterator.hasNext()) {
            Server server = iterator.next();
            builder.append(server.getId()).append(";");
            builder.append(server.getState().getValue()).append(";");
            builder.append(server.getPopulation().getValue()).append(";");
            builder.append(server.isP2p() ? "1" : "0");
            if (iterator.hasNext()) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    public static @Data class Server {
        private int id;
        private ServerState state;
        private ServerPopulation population;
        private boolean p2p;

        public Server(int id, ServerState state, ServerPopulation population, boolean p2p) {
            this.id = id;
            this.state = state;
            this.population = population;
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

    public enum ServerPopulation {
        RECOMMENDED(1),
        MEDIUM(2),
        HIGH(3),
        FULL(4),
        COMING_SOON(99);

        private final int value;

        ServerPopulation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ServerPopulation valueOf(int value) {
            Optional<ServerPopulation> key = Arrays.stream(values())
                    .filter(population -> population.value == value)
                    .findFirst();
            return key.orElse(null);
        }
    }
}


