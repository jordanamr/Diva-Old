package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

import java.util.Optional;

public @Data class WhoisMessage extends ProtocolMessage {

    private boolean self;
    private String name;
    private String nickname;
    private int areaId;

    @Override
    public WhoisMessage deserialize(String data) {
        if (data.isBlank()) {
            self = true;
            return this;
        }
        name = data;
        return this;
    }

    @Override
    public String serialize() {
        return "BWK" + nickname + "|1|" + name + "|" + areaId;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;

        if (self) {
            name = client.getCharacter().getName();
            nickname = client.getNickname();
            areaId = -1; //TODO Set Area ID
            client.sendProtocolMessage(this);
            return true;
        }

        Optional<GameClient> optionalTarget = client.getServer().getClients().stream().filter((c -> c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
        if (optionalTarget.isEmpty()) {
            client.sendPacket("BWE" + name);
            return true;
        }
        GameClient target = optionalTarget.get();
        name = target.getCharacter().getName();
        nickname = target.getNickname();
        areaId = -1; //TODO Set Area ID
        client.sendProtocolMessage(this);
        return true;
    }
}
