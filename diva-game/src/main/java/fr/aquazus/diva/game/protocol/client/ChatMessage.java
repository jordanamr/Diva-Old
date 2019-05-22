package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.network.player.Character;
import fr.aquazus.diva.game.protocol.common.GameActionMessage;
import fr.aquazus.diva.game.protocol.server.CharacterSelectedMessage;
import fr.aquazus.diva.game.protocol.server.CharacterStatsMessage;
import fr.aquazus.diva.game.protocol.server.ImMessage;
import fr.aquazus.diva.game.protocol.server.RestrictionsMessage;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Optional;

public @Data class ChatMessage extends ProtocolMessage {

    private boolean isPrivateMessage = false;
    private String name;
    private Channel channel;
    private String message;

    @Override
    public ChatMessage deserialize(String data) {
        if (data.length() < 3 || !data.contains("|")) return null;
        String[] extraData = data.split("\\|");
        channel = Channel.valueOf(extraData[0].charAt(0));
        if (channel == null) {
            isPrivateMessage = true;
            name = extraData[0];
        }
        message = extraData[1];
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;
        if (isPrivateMessage) {
            Optional<GameClient> optionalTarget = client.getServer().getClients().stream().filter(c -> (c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
            if (optionalTarget.isEmpty()) {
                client.sendPacket("cMEf" + name);
                client.sendPacket("BN");
                return true;
            }
            GameClient target = optionalTarget.get();
            if (client.isChannelEnabled(Channel.WHISPER)) {
                client.sendPacket("cMKT|" + client.getAccountId() + "|" + target.getCharacter().getName() + "|" + message + "|");
                if (target.isChannelEnabled(Channel.WHISPER)) {
                    target.sendPacket("cMKF|" + target.getAccountId() + "|" + client.getCharacter().getName() + "|" + message + "|");
                }
            }
        } else {
            client.getCharacter().talk(channel, message);
        }
        client.sendPacket("BN");
        return true;
    }

    public enum Channel {
        INFO('i'),
        GENERAL('*'),
        WHISPER('#'),
        PARTY('$'),
        TEAM('p'),
        GUILD('%'),
        ALIGNMENT('!'),
        RECRUITMENT('?'),
        TRADE(':'),
        ADMIN('@');

        private final char id;

        Channel(char value) {
            this.id = value;
        }

        public final char getId() {
            return id;
        }

        public static Channel valueOf(char id) {
            Optional<Channel> key = Arrays.stream(values())
                    .filter(state -> state.id == id)
                    .findFirst();
            return key.orElse(null);
        }
    }
}
