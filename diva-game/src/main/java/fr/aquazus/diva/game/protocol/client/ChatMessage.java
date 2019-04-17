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

    private Channel channel;
    private String message;

    @Override
    public ChatMessage deserialize(String data) {
        if (data.length() < 3 || !data.contains("|")) return null;
        String[] extraData = data.split("\\|");
        channel = Channel.valueOf(extraData[0].charAt(0));
        if (channel == null) return null;
        message = extraData[1];
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;
        client.getCharacter().talk(channel, message);
        client.sendPacket("BN");
        return true;
    }

    public enum Channel {
        GENERAL('*');

        private final char value;

        Channel(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        public static Channel valueOf(char value) {
            Optional<Channel> key = Arrays.stream(values())
                    .filter(state -> state.value == value)
                    .findFirst();
            return key.orElse(null);
        }
    }
}
