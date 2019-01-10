package fr.aquazus.diva.auth.network;

import fr.aquazus.diva.auth.AuthServer;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Accounts;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.protocol.ProtocolHandler;
import fr.aquazus.diva.protocol.ProtocolMessage;
import fr.aquazus.diva.protocol.auth.client.AuthConnectMessage;
import fr.aquazus.diva.protocol.auth.client.AuthSearchMessage;
import fr.aquazus.diva.protocol.auth.server.*;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;
import simplenet.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;

@Slf4j
public class AuthClient implements ProtocolHandler {

    private final AuthServer server;
    private final Client netClient;
    private final String ip;
    private String authKey;
    private State state;
    private int accountId;
    private String accountUsername;
    private String accountNickname;
    private String accountSecretQuestion;
    private int accountSubscriptionTime;
    private boolean hasRights;
    private AuthCommunityMessage.Community community;

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
        server.getClients().remove(this);
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() < 2) return false;

        switch (state) {
            case WAIT_VERSION:
                if (!packet.equals(ProtocolHandler.version)) {
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.BAD_VERSION, ProtocolHandler.version));
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
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.BAD_LOGIN));
                    disconnect("Unknown username", username);
                    return true;
                }
                if (!password.equals(accountPojo.getPassword())) {
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.BAD_LOGIN));
                    disconnect("Wrong password", username);
                    return true;
                }
                this.accountId = accountPojo.getId();
                this.accountUsername = accountPojo.getUsername();
                this.accountSecretQuestion = accountPojo.getSecretQuestion();
                this.accountSubscriptionTime = accountPojo.getRemainingSubscription();
                this.hasRights = server.getDatabase().getRanksDao().fetchOneById(accountPojo.getRank()).getConsoleAccess().intValue() == 1;
                this.community = AuthCommunityMessage.Community.valueOf(accountPojo.getCommunity().intValue());
                if (accountPojo.getNickname() == null || accountPojo.getNickname().isBlank()) {
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.CHOOSE_NICKNAME));
                    state = State.WAIT_NICKNAME;
                    return true;
                }
                this.accountNickname = accountPojo.getNickname();
                sendAccountData();
                return true;
            case WAIT_NICKNAME:
                if (packet.equals("Af")) break;
                if (packet.length() < 3 || packet.length() > 20 ||
                        packet.equalsIgnoreCase(this.accountUsername) ||
                        packet.matches("^.*[^a-zA-Z0-9-].*$") ||
                        packet.matches(".*--.*") ||
                        packet.startsWith("-") || packet.endsWith("-") ||
                        packet.chars().filter(ch -> ch == '-').count() > 2 ||
                        Arrays.stream(server.getForbiddenNames()).anyMatch(packet.toLowerCase()::contains) ||
                        server.getDatabase().getAccountsDao().fetchOneByNickname(packet) != null) {
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.NICKNAME_TAKEN));
                    return true;
                }
                server.getDatabase().getDsl().update(ACCOUNTS).set(ACCOUNTS.NICKNAME, packet).where(ACCOUNTS.ID.eq(accountId)).execute();
                this.accountNickname = packet;
                state = State.LOGGING_IN;
                sendAccountData();
                return true;
        }

        if (packet.charAt(0) != 'A') return false;
        switch (packet.charAt(1)) {
            case 'f':
                sendProtocolMessage(new AuthQueueMessage()); //TODO File d'attente
                return true;
            case 'x':
                HashMap<Integer, Integer> characterList = new HashMap<>();
                for (Characters characters : server.getDatabase().getCharactersDao().fetchByAccountId(accountId)) {
                    if (characterList.containsKey(characters.getServer())) {
                        characterList.put(characters.getServer(), characterList.get(characters.getServer()) + 1);
                    } else {
                        characterList.put(characters.getServer(), 1);
                    }
                }
                sendProtocolMessage(new AuthDataMessage(accountSubscriptionTime, characterList));
                state = State.SELECT_SERVER;
                return true;
            case 'F':
                AuthSearchMessage searchMessage = new AuthSearchMessage().deserialize(packet);
                if (searchMessage == null) return false;
                String nickname = searchMessage.getNickname();
                AuthSearchResultMessage result = new AuthSearchResultMessage();
                Accounts friendPojo = server.getDatabase().getAccountsDao().fetchOneByNickname(nickname);
                if (friendPojo == null) {
                    sendProtocolMessage(result);
                    return true;
                }
                HashMap<Integer, Integer> characterCount = new HashMap<>();
                for (Characters characters : server.getDatabase().getCharactersDao().fetchByAccountId(friendPojo.getId())) {
                    if (characterCount.containsKey(characters.getServer())) {
                        characterCount.put(characters.getServer(), characterCount.get(characters.getServer()) + 1);
                    } else {
                        characterCount.put(characters.getServer(), 1);
                    }
                }
                result.setCharacterCount(characterCount);
                sendProtocolMessage(result);
                return true;
            case 'X':
                AuthConnectMessage connectMessage = new AuthConnectMessage().deserialize(packet);
                if (connectMessage == null) return false;
                int serverId = connectMessage.getServerId();
                if (!server.getServersCache().containsKey(serverId)) return false;
                AuthServersMessage.Server serverInstance = server.getServersCache().get(serverId);
                if (serverInstance.getState() != AuthServersMessage.ServerState.ONLINE || !server.getServersIpCache().containsKey(serverId)) {
                    sendProtocolMessage(new AuthConnectErrorMessage(AuthConnectErrorMessage.Type.NOT_AVAILABLE));
                }
                String ticket = server.getCipher().generateTicket();
                server.getRedis().setTicket(serverId, ticket, ip);
                String encryptedAddress = server.getCipher().encodeAXK(server.getServersIpCache().get(serverId));
                sendProtocolMessage(new AuthAddressMessage(encryptedAddress, ticket));
        }
        return false;
    }

    private void sendAccountData() {
        sendProtocolMessage(new AuthNicknameMessage(this.accountNickname));
        sendProtocolMessage(new AuthCommunityMessage(this.community));
        sendProtocolMessage(new AuthServersMessage(new ArrayList<>(server.getServersCache().values())));
        sendProtocolMessage(new AuthRightsMessage(this.hasRights));
        sendProtocolMessage(new AuthQuestionMessage(this.accountSecretQuestion));
    }

    private void log(String message) {
        String format = "[" + ip + (accountUsername == null ? "" : " - " + accountUsername) + "] " + message;
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
        DISCONNECTED
    }
}
