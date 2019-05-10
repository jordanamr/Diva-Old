package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.protocol.server.FriendsListErrorMessage;
import lombok.Data;

import java.util.Optional;

import static fr.aquazus.diva.database.generated.auth.Tables.FRIENDS_LIST;

public @Data class FriendsListDeleteMessage extends ProtocolMessage {

    private FriendsListMessage.Type type;
    private boolean isNickname;
    private String name;

    public FriendsListDeleteMessage(FriendsListMessage.Type type) {
        this.type = type;
    }

    @Override
    public String serialize() {
        return type.getId() + "DK";
    }

    @Override
    public FriendsListDeleteMessage deserialize(String data) {
        if (data.isBlank()) return null;
        isNickname = data.startsWith("*");
        this.name = isNickname ? data.substring(1) : data;
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;

        Optional<Integer> id = Optional.empty();
        if (isNickname && client.getServer().getNicknamesCache().containsValue(name)) {
            for (int key : client.getServer().getNicknamesCache().keySet()) {
                if (client.getServer().getNicknamesCache().get(key).equals(name)) {
                    id = Optional.of(key);
                    break;
                }
            }
        } else {
            Optional<GameClient> optionalTarget = client.getServer().getClients().stream().filter(isNickname ? c -> c.getNickname().equalsIgnoreCase(name) : (c -> c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
            if (optionalTarget.isEmpty()) {
                client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.CANT_ADD_FRIEND_NOT_FOUND));
                return true;
            }
            id = Optional.of(optionalTarget.get().getAccountId());
        }
        if (id.isEmpty()) {
            client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.CANT_ADD_FRIEND_NOT_FOUND));
            return true;
        }
        int fId = id.get();
        switch (type) {
            case FRIENDS:
                if (!client.getFriends().contains(fId)) {
                    client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.CANT_ADD_FRIEND_NOT_FOUND));
                    return true;
                }
                client.getServer().getAuthDatabase().getDsl().deleteFrom(FRIENDS_LIST).where(FRIENDS_LIST.REQUESTER_ID.eq(client.getAccountId())).and(FRIENDS_LIST.RECIPIENT_ID.eq(fId)).and(FRIENDS_LIST.TYPE.eq((byte) 0)).execute();
                client.getFriends().remove(Integer.valueOf(fId));
                break;
            case ENEMIES:
                if (!client.getEnemies().contains(fId)) {
                    client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.CANT_ADD_FRIEND_NOT_FOUND));
                    return true;
                }
                client.getServer().getAuthDatabase().getDsl().deleteFrom(FRIENDS_LIST).where(FRIENDS_LIST.REQUESTER_ID.eq(client.getAccountId())).and(FRIENDS_LIST.RECIPIENT_ID.eq(fId)).and(FRIENDS_LIST.TYPE.eq((byte) 1)).execute();
                client.getEnemies().remove(Integer.valueOf(fId));
                break;
        }

        client.sendProtocolMessage(this);

        return true;
    }
}
