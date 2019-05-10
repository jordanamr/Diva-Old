package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.game.GameServer;
import fr.aquazus.diva.common.protocol.server.ServerMessage;
import fr.aquazus.diva.game.network.player.Character;
import fr.aquazus.diva.game.protocol.client.*;
import fr.aquazus.diva.game.protocol.client.FriendsListMessage;
import fr.aquazus.diva.game.protocol.common.GameActionMessage;
import fr.aquazus.diva.game.protocol.server.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

import java.sql.Timestamp;
import java.util.List;

import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;

@Slf4j
public @Data class GameClient extends DivaClient {

    private final GameServer server;
    private State state;
    private int accountId;
    private String nickname;
    private int remainingSubscription;
    private int characterCount;
    private String secretAnswer;
    private String lastIp;
    private Timestamp lastOnline;
    private Character character;
    private String chatChannels;
    private boolean notificationsFriends;
    private List<Integer> friends;

    public GameClient(GameServer server, Client netClient, String ip) {
        super(netClient, ip);
        this.server = server;
        state = State.INITIALIZING;
        startCommunication();
    }

    @Override
    protected void onReady() {
        state = State.WAIT_TICKET;
        sendPacket("HG");
    }

    @Override
    protected void onDisconnect() {
        state = State.DISCONNECTED;
        server.getClients().remove(this);
        if (character != null) {
            character.leaveCurrentMap();
            character.save();
        }
        save();
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() < 2) return false;

        if (packet.equals("Af")) return true;

        switch (state) {
            case WAIT_TICKET:
                if (!new AuthTicketMessage().handle(this, packet)) {
                    sendProtocolMessage(new ServerMessage(ServerMessage.Type.AUTH, 31));
                    disconnect("Bad ticket packet format", packet);
                }
                return true;
            case CHARACTER_SELECT:
                if (!packet.startsWith("A")) return false;
                switch (packet.charAt(1)) {
                    case 'k':
                        return true;
                    case 'V':
                        sendPacket("AV0");
                        sendPacket("BN");
                        return true;
                    case 'g':
                        //TODO Gifts
                        return true;
                    case 'i':
                        //TODO Identity
                        return true;
                    case 'L':
                        sendProtocolMessage(new CharacterListMessage(remainingSubscription, characterCount, server.getAuthDatabase().getCharactersDao().fetchByAccountId(accountId)));
                        return true;
                    case 'P':
                        sendProtocolMessage(new RandomNameMessage());
                        return true;
                    case 'A':
                        return new CharacterCreationMessage().handle(this, packet);
                    case 'D':
                        return new CharacterDeletionMessage().handle(this, packet);
                    case 'S':
                        return new CharacterSelectionMessage().handle(this, packet);
                    default:
                        return false;
                }
            case INGAME:
                if (packet.startsWith("Ir")) {
                    sendPacket("BN");
                    return true;
                }
                if (packet.equals("ping")) {
                    sendPacket("pong");
                    return true;
                }
                switch (packet.charAt(0)) {
                    case 'e':
                        switch (packet.charAt(1)) {
                            case 'U':
                                return new AttitudeMessage().handle(this, packet);
                            case 'D':
                                return new DirectionMessage().handle(this, packet);
                            default:
                                return false;
                        }
                    case 'F':
                        switch (packet.charAt(1)) {
                            case 'L':
                                return new FriendsListMessage().handle(this, packet);
                            case 'A':
                                return new FriendsListAddMessage().handle(this, packet);
                            case 'D':
                                return new FriendsListDeleteMessage().handle(this, packet);
                            case 'O':
                                return new FriendsListToggleMessage().handle(this, packet);
                            default:
                                return false;
                        }
                    case 'B':
                        switch (packet.charAt(1)) {
                            case 'D':
                                sendProtocolMessage(new DateTimeMessage());
                                return true;
                            case 'M':
                                return new ChatMessage().handle(this, packet);
                            case 'S':
                                return new SmileyMessage().handle(this, packet);
                            default:
                                return false;
                        }
                    case 'G':
                        switch (packet.charAt(1)) {
                            case 'C':
                                if (!packet.equals("GC1")) return false;
                                sendPacket("GCK|1|" + character.getName());
                                return true;
                            case 'I':
                                character.getMapInformations();
                                sendPacket("GDK");
                                sendPacket("BN");
                                //sendPacket("EW+" + character.getId() + "|"); //TODO Job broadcast
                                return true;
                            case 'A':
                                return new GameActionMessage().handle(this, packet);
                            case 'K':
                                switch (packet.charAt(2)) {
                                    case 'K':
                                        if (!packet.equals("GKK0")) return false;
                                        sendPacket("BN");
                                        return true;
                                    case 'E':
                                        return new GameActionCancelMessage().handle(this, packet);
                                    default:
                                        return false;
                                }
                            default:
                                return false;
                        }
                    default:
                        return false;
                }
        }
        return false;
    }

    public void save() {
        server.getAuthDatabase().getDsl().update(ACCOUNTS).set(ACCOUNTS.LAST_IP, this.ip)
                .set(ACCOUNTS.LAST_ONLINE, new Timestamp(System.currentTimeMillis()))
                .set(ACCOUNTS.CHAT_CHANNELS, this.chatChannels).set(ACCOUNTS.NOTIFICATIONS_FRIENDS, (byte) (notificationsFriends ? 1 : 0))
                .where(ACCOUNTS.ID.eq(this.accountId)).execute();
    }

    public enum State {
        INITIALIZING,
        WAIT_TICKET,
        CHARACTER_SELECT,
        INGAME,
        DISCONNECTED
    }
}
