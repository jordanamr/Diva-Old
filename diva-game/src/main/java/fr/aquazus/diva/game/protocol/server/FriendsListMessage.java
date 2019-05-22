package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.network.player.Character;
import lombok.Data;

import java.util.*;
import static fr.aquazus.diva.database.generated.auth.Tables.ACCOUNTS;

public @Data class FriendsListMessage extends ProtocolMessage {

    private Type type;
    private GameClient client;

    public FriendsListMessage(Type type) {
        this.type = type;
    }

    public FriendsListMessage(Type type, GameClient client) {
        this(type);
        this.client = client;
    }

    @Override
    public String serialize() {
        List<Integer> list = new ArrayList<>(type.id == 'F' ? client.getFriends() : client.getEnemies());
        StringBuilder onlineBuilder = new StringBuilder();
        Iterator<GameClient> onlineList = client.getServer().getClients().stream().filter(type.id == 'F' ? f -> client.getFriends().contains(f.getAccountId()) : f -> client.getEnemies().contains(f.getAccountId())).iterator();
        while (onlineList.hasNext()) {
            GameClient f = onlineList.next();
            Character fChar = f.getCharacter();
            if (fChar == null) continue;
            int fId = f.getAccountId();
            boolean mutual = (type.id == 'F' && f.getFriends().contains(client.getAccountId()));
            onlineBuilder.append('|').append(f.getNickname()).append(';');
            onlineBuilder.append(mutual ? "1" : "?").append(';'); //TODO 2 = Sword icon?
            onlineBuilder.append(fChar.getName()).append(';');
            onlineBuilder.append(mutual ? fChar.getLevel() : "?").append(';');
            onlineBuilder.append(mutual ? fChar.getAlignId() : "-1").append(';');
            onlineBuilder.append(fChar.getBreed()).append(';');
            onlineBuilder.append(fChar.getGender()).append(';');
            onlineBuilder.append(fChar.getGfxId());
            list.remove(Integer.valueOf(fId));
        }

        StringBuilder offlineBuilder = new StringBuilder();
        for (int fId : list) {
            String fNick;
            if (client.getServer().getNicknamesCache().containsKey(fId)) {
                fNick = client.getServer().getNicknamesCache().get(fId);
            } else {
                fNick = client.getServer().getAuthDatabase().getDsl().select(ACCOUNTS.NICKNAME).from(ACCOUNTS).where(ACCOUNTS.ID.eq(fId)).fetchOne().value1();
                client.getServer().getNicknamesCache().put(fId, fNick);
            }
            offlineBuilder.append('|').append(fNick);
        }

        return type.id + "L" + offlineBuilder.toString() + onlineBuilder.toString();
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        client = (GameClient) netClient;
        if (packet.length() > 2) return false;
        client.sendProtocolMessage(this);
        return true;
    }

    public enum Type {
        FRIENDS('F'),
        ENEMIES('i');

        private final char id;

        Type(char value) {
            this.id = value;
        }

        public final char getId() {
            return this.id;
        }
    }
}
