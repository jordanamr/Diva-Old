package fr.aquazus.diva.auth.network;

import fr.aquazus.diva.auth.AuthServer;
import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.utils.StringUtils;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Accounts;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.protocol.DivaProtocol;
import fr.aquazus.diva.protocol.auth.client.AuthConnectMessage;
import fr.aquazus.diva.protocol.auth.client.AuthSearchMessage;
import fr.aquazus.diva.protocol.auth.server.*;
import fr.aquazus.diva.protocol.game.server.CharacterCreationErrorMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

import java.util.ArrayList;
import java.util.HashMap;

import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;
import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;

@Slf4j
public class AuthClient extends DivaClient implements DivaProtocol {

    private final AuthServer server;
    private String authKey;
    @Getter
    private State state;
    private int accountId;
    private String accountNickname;
    private String accountSecretQuestion;
    private int accountSubscriptionTime;
    private boolean hasRights;
    private AuthCommunityMessage.Community community;

    public AuthClient(AuthServer server, Client netClient, String ip) {
        super(netClient, ip);
        this.server = server;
        this.state = State.INITIALIZING;
        startCommunication();
    }

    @Override
    protected void onReady() {
        HelloConnectMessage hc = new HelloConnectMessage();
        authKey = hc.getKey();
        state = State.WAIT_VERSION;
        sendProtocolMessage(hc);
    }

    @Override
    protected void onDisconnect() {
        state = State.DISCONNECTED;
        server.getClients().remove(this);
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() <= 1) return false;

        switch (state) {
            case WAIT_VERSION:
                if (!packet.equals(DivaProtocol.version)) {
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.BAD_VERSION, DivaProtocol.version));
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
                if (!StringUtils.isValidNickname(packet)) {
                    sendProtocolMessage(new AuthErrorMessage(AuthErrorMessage.Type.NICKNAME_TAKEN));
                    return true;
                }
                if (server.getDatabase().getDsl().selectFrom(ACCOUNTS).where(ACCOUNTS.NICKNAME.eq(packet)).fetchOne() != null) {
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
                    if (characterList.containsKey(characters.getServerId())) {
                        characterList.put(characters.getServerId(), characterList.get(characters.getServerId()) + 1);
                    } else {
                        characterList.put(characters.getServerId(), 1);
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
                    if (characterCount.containsKey(characters.getServerId())) {
                        characterCount.put(characters.getServerId(), characterCount.get(characters.getServerId()) + 1);
                    } else {
                        characterCount.put(characters.getServerId(), 1);
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
                server.getRedis().setTicket(serverId, accountId, ip, ticket);
                String encryptedAddress = server.getCipher().encodeAXK(server.getServersIpCache().get(serverId));
                sendProtocolMessage(new AuthAddressMessage(encryptedAddress, ticket));
                return true;
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

    public void updateServersData() {
        sendProtocolMessage(new AuthServersMessage(new ArrayList<>(server.getServersCache().values())));
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
