package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

import java.util.Arrays;
import java.util.Optional;

public @Data class ChannelToggleMessage extends ProtocolMessage {

    private boolean newStatus;
    private char[] channels;

    @Override
    public ChannelToggleMessage deserialize(String data) {
        if (data.length() < 2 || (data.charAt(0) != '+' && data.charAt(0) != '-')) return null;
        newStatus = data.charAt(0) == '+';
        channels = data.substring(1).toCharArray();
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;
        for (char channelId : channels) {
            ChatMessage.Channel channel = ChatMessage.Channel.valueOf(channelId);
            if (channel == null) continue;
            if (newStatus) {
                client.enableChannel(channel);
                client.sendPacket("cC+" + channel.getId());
            } else {
                client.disableChannel(channel);
                client.sendPacket("cC-" + channel.getId());
            }
        }
        return true;
    }
}
