package fr.aquazus.diva.common.network;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;
import simplenet.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public abstract class DivaClient {

    private boolean disconnected = false;
    private Client netClient;
    protected String ip;

    protected DivaClient(Client netClient, String ip) {
        this.netClient = netClient;
        this.ip = ip;
    }

    protected void startCommunication() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        netClient.readByteAlways(data -> {
            if (data == (byte) 0) {
                String packet = new String(stream.toByteArray(), StandardCharsets.UTF_8);
                if (packet.length() <= 1) return;
                packet = packet.substring(0, packet.length() - 1);
                stream.reset();
                this.log("<-- " + packet);
                if (!disconnected && netClient.getChannel().isOpen() && !handlePacket(packet)) {
                    disconnect("Malformed or unimplemented packet", packet);
                }
                return;
            }
            stream.write(data);
        });
        netClient.preDisconnect(this::disconnect);
        onReady();
    }

    protected void log(String message) {
        String format = "[" + ip + "] " + message;
        if (message.startsWith("-->") || message.startsWith("<--")) {
            log.debug(format);
        } else {
            log.info(format);
        }
    }

    protected void sendProtocolMessage(ProtocolMessage message) {
        sendPacket(message.serialize());
    }

    protected void sendPacket(String packet) {
        try {
            byte[] data = (packet + "\0").getBytes(StandardCharsets.UTF_8);
            this.log("--> " + packet);
            for (int bound = 0; bound < data.length; bound += 1024) {
                int end = Math.min(data.length, bound + 1024);
                Packet.builder().putBytes(Arrays.copyOfRange(data, bound, end)).writeAndFlush(netClient);
            }
        } catch (Exception ex) {
            log.error("An error occurred while splitting a packet", ex);
            disconnect("Packet splitting exception");
        }
    }

    protected void disconnect(String... reason) {
        if (disconnected) return;
        disconnected = true;
        log("disconnected!" + (reason.length != 0 ? " " + Arrays.toString(reason) : ""));
        if (netClient.getChannel().isOpen()) netClient.close();
        onDisconnect();
    }

    protected boolean handlePacket(String packet) {
        throw new UnsupportedOperationException();
    }

    protected void onDisconnect() {
        throw new UnsupportedOperationException();
    }

    protected void onReady() {
        throw new UnsupportedOperationException();
    }
}
