package fr.aquazus.diva.auth.network;

import fr.aquazus.diva.protocol.ProtocolHandler;
import fr.aquazus.diva.protocol.ProtocolMessage;
import fr.aquazus.diva.protocol.auth.server.AccountLoginErrorMessage;
import fr.aquazus.diva.protocol.auth.server.HelloConnectMessage;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;
import simplenet.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AuthClient implements ProtocolHandler {

    private final Client netClient;
    private final String ip;
    private String authKey;
    private State state;

    public AuthClient(Client netClient, String ip) {
        this.netClient = netClient;
        this.ip = ip;
        this.state = State.INITIALIZING;
        startCommunication();
    }

    private void startCommunication() {
        HelloConnectMessage hc = new HelloConnectMessage();
        authKey = hc.getKey();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        netClient.readByteAlways(data -> {
            if (data == (byte) 0) {
                String packet = new String(stream.toByteArray(), StandardCharsets.UTF_8);
                packet = packet.substring(0, packet.length() - 1);
                stream.reset();
                this.log("--> " + packet);
                if (state != State.DISCONNECTED && netClient.getChannel().isOpen() && !handlePacket(packet)) {
                    disconnect();
                }
                return;
            }
            stream.write(data);
        });

        netClient.preDisconnect(this::disconnect);

        state = State.WAIT_VERSION;
        sendProtocolMessage(hc);
    }

    public void disconnect() {
        if (state == State.DISCONNECTED) {
            return;
        }
        this.log("disconnected!");
        state = State.DISCONNECTED;
        netClient.close();
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() < 2) return false;

        switch (state) {
            case WAIT_VERSION:
                if (!packet.equals(ProtocolHandler.version)) {
                    sendProtocolMessage(new AccountLoginErrorMessage(AccountLoginErrorMessage.Type.BAD_VERSION, ProtocolHandler.version));
                    return false;
                }
                return true;
        }

        switch (packet.charAt(0)) {
            case 'A':
                /*switch (packet.charAt(1)) {

                }*/
        }
        return false;
    }

    private void log(String message) {
        String format = "[" + ip + "] " + message;
        if (message.startsWith("-->") || message.startsWith("<--")) {
            log.debug(format);
        } else {
            log.info(format);
        }
    }

    private void sendProtocolMessage(ProtocolMessage message) {
        try {
            String packet = new String(message.serialize().getBytes(), StandardCharsets.UTF_8);
            StringReader reader = new StringReader(packet);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(2048);
            OutputStreamWriter writer = new OutputStreamWriter(buffer, StandardCharsets.UTF_8);
            char[] cbuf = new char[1024];
            byte[] tempBuf;
            int len;
            while ((len = reader.read(cbuf, 0, cbuf.length)) > 0) {
                writer.write(cbuf, 0, len);
                writer.flush();
                if (buffer.size() >= 1024) {
                    tempBuf = buffer.toByteArray();
                    Packet.builder().putBytes(buffer.toByteArray()).writeAndFlush(netClient);
                    buffer.reset();
                    if (tempBuf.length > 1024) {
                        buffer.write(tempBuf, 1024, tempBuf.length - 1024);
                    }
                }
            }
            Packet.builder().putBytes(buffer.toByteArray()).putByte(0).writeAndFlush(netClient);
        } catch (Exception ex) {
            log.error("An error occurred while splitting a packet", ex);
        }
    }

    public enum State {
        INITIALIZING,
        WAIT_VERSION,
        WAIT_CREDENTIALS,
        SELECT_SERVER,
        SELECT_CHARACTER,
        DISCONNECTED
    }
}
