package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.utils.StringUtils;
import fr.aquazus.diva.game.GameServer;
import fr.aquazus.diva.protocol.common.server.ServerMessage;
import fr.aquazus.diva.protocol.game.server.CharacterCreationErrorMessage;
import fr.aquazus.diva.protocol.game.server.RandomNameMessage;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;

@Slf4j
public class GameClient extends DivaClient {

    private final GameServer server;
    private State state;
    private int accountId;

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
    }

    @Override
    public boolean handlePacket(String packet) {
        if (packet.length() <= 1) return false;

        switch (state) {
            case WAIT_TICKET:
                if (packet.length() != 13 || !packet.startsWith("AT")) {
                    sendProtocolMessage(new ServerMessage(ServerMessage.Type.AUTH, 31));
                    disconnect("Bad ticket packet format", packet);
                    return true;
                }
                String ticketData = ip + "|" + packet.substring(2);
                if (!server.getTicketsCache().containsKey(ticketData)) {
                    sendProtocolMessage(new ServerMessage(ServerMessage.Type.AUTH, 31));
                    disconnect("Invalid ticket", packet.substring(2));
                    return true;
                }
                this.accountId = server.getTicketsCache().get(ticketData);
                server.getTicketsCache().remove(ticketData);
                log("Login successful");
                state = State.CHARACTER_SELECT;
                sendPacket("ATK0"); //TODO: Implement ATK in protocol module, ATK + base16 (Nbr>0) + HashKey
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
                        //TODO Character list
                        sendPacket("ALK");
                        return true;
                    case 'f':
                        sendPacket("BN");
                        sendPacket("BN");
                        sendPacket("BN");
                        //TODO File d'attente
                        return true;
                    case 'P':
                        sendProtocolMessage(new RandomNameMessage());
                        return true;
                    case 'A':
                        if (!packet.contains("|") || packet.length() <= 12) return false;
                        String[] characterData = packet.substring(2).split("\\|");
                        if (characterData.length != 6) return false;
                        String characterName = characterData[0];
                        if (!StringUtils.isValidName(characterName)) {
                            sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_INVALID));
                            return true;
                        }
                        if (server.getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.SERVER.eq(server.getId())).and(CHARACTERS.NAME.eq(characterName)).fetchOne() != null) {
                            sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_TAKEN));
                            return true;
                        }
                        return true;
                }
        }
        return true;
    }

    public enum State {
        INITIALIZING,
        WAIT_TICKET,
        CHARACTER_SELECT,
        DISCONNECTED
    }
}
