package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.common.utils.StringUtils;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Accounts;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.game.GameServer;
import fr.aquazus.diva.common.protocol.server.ServerMessage;
import fr.aquazus.diva.game.network.player.Character;
import fr.aquazus.diva.game.protocol.client.CharacterDeletionMessage;
import fr.aquazus.diva.game.protocol.client.CharacterSelectionMessage;
import fr.aquazus.diva.game.protocol.client.GameActionMessage;
import fr.aquazus.diva.game.protocol.server.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Protocol;
import simplenet.Client;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;
import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;

@Slf4j
public class GameClient extends DivaClient {

    @Getter
    private final GameServer server;
    private State state;
    @Getter
    private int accountId;
    private int remainingSubscription;
    private int characterCount;
    private String secretAnswer;
    private String lastIp;
    private Timestamp lastOnline;
    private Character character;
    private String chatChannels;
    private boolean notificationsFriends;

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
                if (server.isAccountOnline(server.getTicketsCache().get(ticketData))) {
                    sendPacket("AlEc");
                    disconnect("Already logged in", "" + server.getTicketsCache().get(ticketData));
                    return true;
                }
                this.accountId = server.getTicketsCache().get(ticketData);
                server.getTicketsCache().remove(ticketData);

                Accounts accountPojo = server.getAuthDatabase().getAccountsDao().fetchOneById(accountId);
                this.remainingSubscription = accountPojo.getRemainingSubscription();
                this.secretAnswer = accountPojo.getSecretAnswer();
                this.characterCount = server.getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.ACCOUNT_ID.eq(accountId)).execute();
                this.lastIp = accountPojo.getLastIp();
                this.lastOnline = accountPojo.getLastOnline();
                this.chatChannels = accountPojo.getChatChannels();
                this.notificationsFriends = accountPojo.getNotificationsFriends() == (byte) 1;

                log("Login successful");
                state = State.CHARACTER_SELECT;
                sendPacket("ATK0"); //TODO: Implement ATK in protocol module, ATK + base16 (Nbr>0) + HashKey
                return true;
            case CHARACTER_SELECT:
                if (!packet.startsWith("A")) return false;
                switch (packet.charAt(0)) {
                    case 'A':
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
                                if (!packet.contains("|") || packet.length() <= 12) return false;
                                String[] characterData = packet.substring(2).split("\\|");
                                if (characterData.length != 6) return false;
                                String characterName = characterData[0];
                                if (!StringUtils.isValidCharacterName(characterName)) {
                                    sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_INVALID));
                                    return true;
                                }
                                if (server.getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.SERVER_ID.eq(server.getId())).and(CHARACTERS.NAME.eq(characterName)).fetchOne() != null) {
                                    sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_TAKEN));
                                    return true;
                                }
                                byte characterBreed, characterGender;
                                int characterColor1, characterColor2, characterColor3;
                                try {
                                    characterBreed = Byte.parseByte(characterData[1]);
                                    characterGender = Byte.parseByte(characterData[2]);
                                    characterColor1 = Integer.parseInt(characterData[3]);
                                    characterColor2 = Integer.parseInt(characterData[4]);
                                    characterColor3 = Integer.parseInt(characterData[5]);
                                } catch (NumberFormatException ex) {
                                    sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.DATA_INVALID));
                                    return true;
                                }
                                boolean invalidData = false;
                                if (characterBreed < 1 || characterBreed > 12) invalidData = true;
                                if (characterGender != 0 && characterGender != 1) invalidData = true;
                                if (characterColor1 < -1 || characterColor1 > 16777215) invalidData = true;
                                if (characterColor2 < -1 || characterColor2 > 16777215) invalidData = true;
                                if (characterColor3 < -1 || characterColor3 > 16777215) invalidData = true;
                                if (invalidData) {
                                    sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.DATA_INVALID));
                                    return true;
                                }
                                if (characterCount >= 5) {
                                    sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NO_EMPTY_SLOT));
                                    return true;
                                }
                                server.getAuthDatabase().getDsl().insertInto(CHARACTERS).set(CHARACTERS.ACCOUNT_ID, accountId)
                                        .set(CHARACTERS.SERVER_ID, server.getId()).set(CHARACTERS.NAME, characterName)
                                        .set(CHARACTERS.BREED, characterBreed).set(CHARACTERS.GENDER, characterGender)
                                        .set(CHARACTERS.GFX_ID, Short.parseShort(characterBreed + "" + characterGender))
                                        .set(CHARACTERS.COLOR1, characterColor1).set(CHARACTERS.COLOR2, characterColor2)
                                        .set(CHARACTERS.COLOR3, characterColor3).set(CHARACTERS.LEVEL, server.getConfig().getStartLevel())
                                        .set(CHARACTERS.KAMAS, server.getConfig().getStartKamas()).set(CHARACTERS.MAP_ID, server.getConfig().getStartMapId())
                                        .set(CHARACTERS.CELL_ID, server.getConfig().getStartMapCell()).execute();
                                characterCount++;
                                sendPacket("BN");
                                sendPacket("AAK");
                                sendProtocolMessage(new CharacterListMessage(remainingSubscription, characterCount, server.getAuthDatabase().getCharactersDao().fetchByAccountId(accountId)));
                                return true;
                            case 'D':
                                CharacterDeletionMessage deletionMessage = new CharacterDeletionMessage().deserialize(packet);
                                if (deletionMessage == null) return false;
                                Characters characterToDelete = server.getAuthDatabase().getCharactersDao().fetchOneById(deletionMessage.getCharacterId());
                                if (characterToDelete == null) {
                                    disconnect("Trying to delete an unknown character", "" + deletionMessage.getCharacterId());
                                    return true;
                                }
                                if (characterToDelete.getAccountId() != accountId) {
                                    disconnect("Trying to delete someone else's character", "" + deletionMessage.getCharacterId());
                                    return true;
                                }
                                if (characterToDelete.getLevel() >= 20 && !deletionMessage.getSecretAnswer().equalsIgnoreCase(secretAnswer)) {
                                    sendPacket("ADE");
                                    return true;
                                }
                                server.getAuthDatabase().getCharactersDao().delete(characterToDelete);
                                characterCount--;
                                sendPacket("BN");
                                sendProtocolMessage(new CharacterListMessage(remainingSubscription, characterCount, server.getAuthDatabase().getCharactersDao().fetchByAccountId(accountId)));
                                return true;
                            case 'S':
                                CharacterSelectionMessage selectionMessage = new CharacterSelectionMessage().deserialize(packet);
                                if (selectionMessage == null) return false;
                                Characters characterToUse = server.getAuthDatabase().getCharactersDao().fetchOneById(selectionMessage.getCharacterId());
                                if (characterToUse == null) {
                                    disconnect("Trying to use an unknown character", "" + selectionMessage.getCharacterId());
                                    return true;
                                }
                                if (characterToUse.getAccountId() != accountId) {
                                    disconnect("Trying to use someone else's character", "" + selectionMessage.getCharacterId());
                                    return true;
                                }
                                this.character = new Character(this, characterToUse);
                                this.state = State.INGAME;
                                sendProtocolMessage(new CharacterStatsMessage(character));
                                sendPacket("Rx0"); //TODO Mount XP
                                sendProtocolMessage(new CharacterSelectedMessage(character));
                                /** TODO
                                 *  ItemSet Bonuses
                                 *  Gobball set (id 1) full :
                                 *  OS+1|2425;2416;2428;2419;2422;2411;2414|76#32#0#0,7e#32#0#0,70#5#0#0,7c#14#0#0,6f#1#0#0,6e#1e#0#0
                                 *
                                 *  Adventurer set (id 1) only ring :
                                 *  OS+5|2475|
                                 */
                                sendPacket("ZS1"); //TODO Align order id
                                sendPacket("cC+" + chatChannels);
                                /** TODO
                                 *  SubAreas list for worldmap
                                 *  al|270;0|49;1|......
                                 *
                                 *  id;align
                                 */
                                sendPacket("SLo+"); //Can see next spells & use spell points
                                /** TODO
                                 *  Spells list
                                 *
                                 *  SL164~1~b;169~1~c;161~3~d;163~1~e;165~5~f;172~3~g;167~1~h;168~1~i;162~3~j;
                                 *
                                 *  id~level~position
                                 */
                                sendProtocolMessage(new RestrictionsMessage(character));
                                sendPacket("Ow0|" + character.getStats().getPods());
                                sendPacket("FO" + (notificationsFriends ? "+" : "-"));
                                sendProtocolMessage(new ImMessage("189"));
                                if (lastIp != null && lastOnline != null) sendProtocolMessage(new ImMessage("0152", new SimpleDateFormat("yyyy~MM~dd~HH~mm~").format(lastOnline) + this.lastIp));
                                sendProtocolMessage(new ImMessage("0153", this.ip));
                                sendPacket("ILS2000");
                                character.joinMap(characterToUse.getMapId(), characterToUse.getCellId());
                                sendPacket("BT" + System.currentTimeMillis());
                                sendPacket("fC0"); //TODO Map fight count
                                return true;
                            default:
                                return false;
                        }
                }
            case INGAME:
                if (packet.startsWith("Ir")) return true;
                switch (packet.charAt(0)) {
                    case 'B':
                        switch (packet.charAt(1)) {
                            case 'D':
                                sendProtocolMessage(new DateTimeMessage());
                                return true;
                            case 'M':
                                String[] extraData = packet.split("\\|");
                                if (packet.charAt(2) == '*') {
                                    if (character.getCurrentMap() == null || extraData[1].length() < 1) return true;
                                    character.sendMessage("K", extraData[1]);
                                    return true;
                                }
                            case 'S':
                                character.sendSmiley(Integer.parseInt(packet.substring(2)));
                                return true;
                            default:
                                return false;
                        }
                    case 'G':
                        switch (packet.charAt(1)) {
                            case 'C':
                                if (!packet.substring(2).equals("1")) {
                                    disconnect("Invalid game type", packet.substring(2));
                                    return true;
                                }
                                sendPacket("GCK|1|" + character.getName());
                                return true;
                            case 'I':
                                character.getMapInformations();
                                sendPacket("GDK");
                                sendPacket("BN");
                                //sendPacket("EW+" + character.getId() + "|"); //TODO Job broadcast
                                return true;
                            case 'A':
                                GameActionMessage gameAction = new GameActionMessage().deserialize(packet.substring(2));
                                if (gameAction.getAction() == null) {
                                    disconnect("Invalid GameAction", "" + packet.substring(2));
                                    return true;
                                }
                                switch (gameAction.getAction()) {
                                    case MOVEMENT:
                                        character.broadcastMovement(gameAction.makeNewPath(character.getCellId()));
                                }
                                return true;
                            case 'K':
                                switch (packet.charAt(2)) {
                                    case 'K':
                                        if (packet.charAt(3) != '0') return false;
                                        sendPacket("BN");
                                        return true;
                                    case 'E':
                                        String[] extraData = packet.substring(3).split("\\|");
                                        int actionId = Integer.parseInt(extraData[0]);
                                        switch (actionId) {
                                            case 0:
                                                character.setCellId(Integer.parseInt(extraData[1]));
                                                sendPacket("BN");
                                                return true;
                                            default:
                                                return false;
                                        }
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
        return true;
    }

    public void sendProtocolMessage(ProtocolMessage message) {
        super.sendProtocolMessage(message);
    }

    public void sendPacket(String packet) {
        super.sendPacket(packet);
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
