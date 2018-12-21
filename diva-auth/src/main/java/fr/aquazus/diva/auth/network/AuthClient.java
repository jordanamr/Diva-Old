package fr.aquazus.diva.auth.network;

import fr.aquazus.diva.auth.AuthServer;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Accounts;
import fr.aquazus.diva.protocol.ProtocolHandler;
import fr.aquazus.diva.protocol.ProtocolMessage;
import fr.aquazus.diva.protocol.auth.server.AccountLoginErrorMessage;
import fr.aquazus.diva.protocol.auth.server.AccountLoginQueueMessage;
import fr.aquazus.diva.protocol.auth.server.HelloConnectMessage;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;
import simplenet.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;
import static fr.aquazus.diva.protocol.auth.server.AccountLoginErrorMessage.Type;

@Slf4j
public class AuthClient implements ProtocolHandler {

    private final AuthServer server;
    private final Client netClient;
    private final String ip;
    private String authKey;
    private State state;
    private int accountId;
    private String accountUsername;

    public AuthClient(AuthServer server, Client netClient, String ip) {
        this.server = server;
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
                this.log("<-- " + packet);
                if (state != State.DISCONNECTED && netClient.getChannel().isOpen() && !handlePacket(packet)) {
                    disconnect("Malformed or unimplemented packet", packet);
                }
                return;
            }
            stream.write(data);
        });

        netClient.preDisconnect(this::disconnect);

        state = State.WAIT_VERSION;
        sendProtocolMessage(hc);
    }

    private void disconnect(String... reason) {
        if (state == State.DISCONNECTED) {
            return;
        }
        this.log("disconnected!" + (reason.length != 0 ? " " + Arrays.toString(reason) : ""));
        state = State.DISCONNECTED;
        if (netClient.getChannel().isOpen()) netClient.close();
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() < 2) return false;

        switch (state) {
            case WAIT_VERSION:
                if (!packet.equals(ProtocolHandler.version)) {
                    sendProtocolMessage(new AccountLoginErrorMessage(Type.BAD_VERSION, ProtocolHandler.version));
                    disconnect("Wrong client version", packet);
                    return true;
                }
                state = State.WAIT_CREDENTIALS;
                return true;
            case WAIT_CREDENTIALS:
                state = State.LOGGING_IN;
                if (packet.equals("Af")) break;
                if (!packet.contains("\n")) return false;
                String[] extraData = packet.split("\n");
                String username = extraData[0];
                String password = extraData[1];
                if (username == null || username.isEmpty() || password == null || password.isEmpty()) return false;
                if (!password.startsWith("#1")) return false;
                password = server.getCipher().decodePassword(password, authKey);
                Accounts accountPojo = server.getDatabase().getAccountsDao().fetchOneByUsername(username);
                if (accountPojo == null) {
                    sendProtocolMessage(new AccountLoginErrorMessage(Type.BAD_LOGIN));
                    disconnect("Unknown username", username);
                    return true;
                }
                if (!password.equals(accountPojo.getPassword())) {
                    sendProtocolMessage(new AccountLoginErrorMessage(Type.BAD_LOGIN));
                    disconnect("Wrong password", username);
                    return true;
                }
                this.accountId = accountPojo.getId();
                this.accountUsername = accountPojo.getUsername();
                if (accountPojo.getNickname() == null) {
                    sendProtocolMessage(new AccountLoginErrorMessage(Type.CHOOSE_NICKNAME));
                    state = State.WAIT_NICKNAME;
                    return true;
                }
                return false;
            case WAIT_NICKNAME:
                if (packet.equals("Af")) break;
                if (packet.length() < 3 || packet.length() > 20 ||
                        packet.equalsIgnoreCase(this.accountUsername) ||
                        packet.matches("^.*[^a-zA-Z0-9-].*$") ||
                        packet.matches(".*--.*") ||
                        packet.startsWith("-") || packet.endsWith("-") ||
                        Arrays.stream(server.getForbiddenNames()).anyMatch(packet.toLowerCase()::contains) ||
                        server.getDatabase().getAccountsDao().fetchOneByNickname(packet) != null) {
                    sendProtocolMessage(new AccountLoginErrorMessage(Type.NICKNAME_TAKEN));
                    return true;
                }
                server.getDatabase().getDsl().update(ACCOUNTS).set(ACCOUNTS.NICKNAME, packet).where(ACCOUNTS.ID.eq(accountId)).execute();
                //TODO Next
                return false;
        }

        switch (packet.charAt(0)) {
            case 'A':
                switch (packet.charAt(1)) {
                    case 'f':
                        //TODO File d'attente
                        sendProtocolMessage(new AccountLoginQueueMessage());
                        return true;
                }
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
        sendPacket(message.serialize());
    }

    private void sendPacket(String packet) {
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

    public enum State {
        INITIALIZING,
        WAIT_VERSION,
        WAIT_CREDENTIALS,
        LOGGING_IN,
        WAIT_NICKNAME,
        SELECT_SERVER,
        SELECT_CHARACTER,
        DISCONNECTED
    }
}
