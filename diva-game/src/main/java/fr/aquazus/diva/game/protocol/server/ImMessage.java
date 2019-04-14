package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;

import java.util.Arrays;
import java.util.Iterator;

public class ImMessage extends ProtocolMessage {

    private String id;
    private String[] args;

    public ImMessage(String id, String... args) {
        this.id = id;
        this.args = args;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("Im");
        builder.append(id);
        if (args.length != 0) {
            builder.append(";");
            Iterator<String> iterator = Arrays.stream(args).iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
                if (iterator.hasNext()) builder.append(";");
            }
        }
        return builder.toString();
    }
}
