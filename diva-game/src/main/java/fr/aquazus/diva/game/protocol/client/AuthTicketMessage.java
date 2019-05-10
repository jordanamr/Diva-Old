package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.common.protocol.server.ServerMessage;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Accounts;
import fr.aquazus.diva.game.network.GameClient;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;

import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;
import static fr.aquazus.diva.database.generated.auth.Tables.FRIENDS;

public @Data class AuthTicketMessage extends ProtocolMessage {

    private String ticket;

    @Override
    public AuthTicketMessage deserialize(String data) {
        if (data.length() != 13 || !data.startsWith("AT")) return null;
        ticket = data.substring(2);
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        String ticketData = client.getIp() + "|" + packet.substring(2);
        if (!client.getServer().getTicketsCache().containsKey(ticketData)) {
            client.sendProtocolMessage(new ServerMessage(ServerMessage.Type.AUTH, 31));
            client.disconnect("Invalid ticket", packet.substring(2));
            return true;
        }
        if (client.getServer().isAccountOnline(client.getServer().getTicketsCache().get(ticketData))) {
            client.sendPacket("AlEc");
            client.disconnect("Already logged in", "" + client.getServer().getTicketsCache().get(ticketData));
            return true;
        }
        client.setAccountId(client.getServer().getTicketsCache().get(ticketData));
        client.getServer().getTicketsCache().remove(ticketData);

        Accounts accountPojo = client.getServer().getAuthDatabase().getAccountsDao().fetchOneById(client.getAccountId());
        client.setNickname(accountPojo.getNickname());
        client.setRemainingSubscription(accountPojo.getRemainingSubscription());
        client.setSecretAnswer(accountPojo.getSecretAnswer());
        client.setCharacterCount(client.getServer().getAuthDatabase().getDsl().selectFrom(CHARACTERS).where(CHARACTERS.ACCOUNT_ID.eq(client.getAccountId())).execute());
        client.setLastIp(accountPojo.getLastIp());
        client.setLastOnline(accountPojo.getLastOnline());
        client.setChatChannels(accountPojo.getChatChannels());
        client.setNotificationsFriends(accountPojo.getNotificationsFriends() == (byte) 1);
        client.setFriends(Collections.synchronizedList(new ArrayList<>()));
        client.getServer().getAuthDatabase().getDsl().select(FRIENDS.RECIPIENT_ID).from(FRIENDS).where(FRIENDS.REQUESTER_ID.eq(client.getAccountId())).iterator().forEachRemaining(record -> client.getFriends().add(record.value1()));

        client.log("Login successful");
        client.setState(GameClient.State.CHARACTER_SELECT);
        client.sendPacket("ATK0"); //TODO: Implement ATK in protocol module, ATK + base16 (Nbr>0) + HashKey
        return true;
    }
}
