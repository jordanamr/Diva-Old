package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.network.player.Character;
import lombok.Data;

import java.util.*;
import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;

public @Data class FriendsListMessage extends ProtocolMessage {

    private GameClient client;

    public FriendsListMessage() { }

    public FriendsListMessage(GameClient client) {
        this.client = client;
    }

    @Override
    public String serialize() {
        List<Integer> friendsList = new ArrayList<>(client.getFriends());
        StringBuilder onlineBuilder = new StringBuilder();
        Iterator<GameClient> onlineFriends = client.getServer().getClients().stream().filter(f -> client.getFriends().contains(f.getAccountId())).iterator();
        while (onlineFriends.hasNext()) {
            GameClient f = onlineFriends.next();
            Character fChar = f.getCharacter();
            if (fChar == null) continue;
            int fId = f.getAccountId();
            boolean mutual = f.getFriends().contains(client.getAccountId());
            onlineBuilder.append('|').append(f.getNickname()).append(';');
            onlineBuilder.append(mutual ? "1" : "?").append(';'); //TODO 2 = Sword icon?
            onlineBuilder.append(fChar.getName()).append(';');
            onlineBuilder.append(mutual ? fChar.getLevel() : "?").append(';');
            onlineBuilder.append(mutual ? fChar.getAlignId() : "-1").append(';');
            onlineBuilder.append(fChar.getBreed()).append(';');
            onlineBuilder.append(fChar.getGender()).append(';');
            onlineBuilder.append(fChar.getGfxId());
            friendsList.remove(Integer.valueOf(fId));
        }

        StringBuilder offlineBuilder = new StringBuilder();
        Iterator<Integer> offlineFriends = friendsList.iterator();
        while (offlineFriends.hasNext()) {
            int fId = offlineFriends.next();
            String fNick;
            if (client.getServer().getNicknamesCache().containsKey(fId)) {
                fNick = client.getServer().getNicknamesCache().get(fId);
            } else {
                fNick = client.getServer().getAuthDatabase().getDsl().select(ACCOUNTS.NICKNAME).from(ACCOUNTS).where(ACCOUNTS.ID.eq(fId)).fetchOne().value1();
                client.getServer().getNicknamesCache().put(fId, fNick);
            }
            offlineBuilder.append(fNick);
            if (offlineFriends.hasNext()) offlineBuilder.append('|');
        }

        return "FL|" + offlineBuilder.toString() + onlineBuilder.toString();
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        client = (GameClient) netClient;
        if (packet.length() > 2) return false;
        client.sendProtocolMessage(this);
        return true;
    }
}
