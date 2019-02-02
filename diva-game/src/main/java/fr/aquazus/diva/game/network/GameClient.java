package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.utils.StringUtils;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Accounts;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.game.GameServer;
import fr.aquazus.diva.protocol.common.server.ServerMessage;
import fr.aquazus.diva.protocol.game.client.CharacterDeletionMessage;
import fr.aquazus.diva.protocol.game.client.CharacterSelectionMessage;
import fr.aquazus.diva.protocol.game.server.CharacterCreationErrorMessage;
import fr.aquazus.diva.protocol.game.server.CharacterListMessage;
import fr.aquazus.diva.protocol.game.server.RandomNameMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.Client;

import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;

@Slf4j
public class GameClient extends DivaClient {

    @Getter
    private final GameServer server;
    private State state;
    private int accountId;
    private int remainingSubscription;
    private int characterCount;
    private String secretAnswer;

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

                Accounts accountPojo = server.getAuthDatabase().getAccountsDao().fetchOneById(accountId);
                this.remainingSubscription = accountPojo.getRemainingSubscription();
                this.secretAnswer = accountPojo.getSecretAnswer();
                this.characterCount = server.getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.ACCOUNT_ID.eq(accountId)).execute();

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
                        sendProtocolMessage(new CharacterListMessage(remainingSubscription, characterCount, server.getAuthDatabase().getCharactersDao().fetchByAccountId(accountId)));
                        return true;
                    case 'f':
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
                        if (!StringUtils.isValidCharacterName(characterName)) {
                            sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_INVALID));
                            return true;
                        }
                        if (server.getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.SERVER_ID.eq(server.getId())).and(CHARACTERS.NAME.eq(characterName)).fetchOne() != null) {
                            sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.NAME_TAKEN));
                            return true;
                        }
                        byte characterClass, characterGender;
                        int characterColor1, characterColor2, characterColor3;
                        try {
                            characterClass = Byte.parseByte(characterData[1]);
                            characterGender = Byte.parseByte(characterData[2]);
                            characterColor1 = Integer.parseInt(characterData[3]);
                            characterColor2 = Integer.parseInt(characterData[4]);
                            characterColor3 = Integer.parseInt(characterData[5]);
                        } catch (NumberFormatException ex) {
                            sendProtocolMessage(new CharacterCreationErrorMessage(CharacterCreationErrorMessage.Type.DATA_INVALID));
                            return true;
                        }
                        boolean invalidData = false;
                        if (characterClass < 1 || characterClass > 12) invalidData = true;
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
                                .set(CHARACTERS.CLASS, characterClass).set(CHARACTERS.GENDER, characterGender)
                                .set(CHARACTERS.GFX_ID, Integer.parseInt(characterClass + "" + characterGender))
                                .set(CHARACTERS.COLOR1, characterColor1).set(CHARACTERS.COLOR2, characterColor2)
                                .set(CHARACTERS.COLOR3, characterColor3).execute();
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

                    default:
                        return false;
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
