package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.protocol.server.FriendsListErrorMessage;
import lombok.Data;

import java.util.Optional;
import static fr.aquazus.diva.database.generated.auth.Tables.FRIENDS_LIST;

public @Data class FriendsListAddMessage extends ProtocolMessage {

    private FriendsListMessage.Type type;
    private boolean isCatchAll;
    private boolean isNickname;
    private String name;
    private GameClient target;

    public FriendsListAddMessage(FriendsListMessage.Type type) {
        this.type = type;
    }

    @Override
    public String serialize() {
        return type.getId() + "AK" + target.getNickname() + ';' +
                '2' + ';' + //TODO 2 = Sword icon?
                target.getCharacter().getName() + ';' +
                target.getCharacter().getLevel() + ';' +
                target.getCharacter().getBreed() + ';' +
                target.getCharacter().getGender() + ';' +
                target.getCharacter().getGfxId() + ';';
    }

    @Override
    public FriendsListAddMessage deserialize(String data) {
        if (data.isBlank()) return null;
        isNickname = data.startsWith("*");
        isCatchAll = data.startsWith("%");
        this.name = (isNickname || isCatchAll) ? data.substring(1) : data;
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet.substring(2)) == null) return false;

        if (((!isNickname || isCatchAll) && client.getCharacter().getName().equalsIgnoreCase(name)) || ((isNickname || isCatchAll) && client.getNickname().equalsIgnoreCase(name))) {
            client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.CANT_ADD_YOU));
            return true;
        }

        if ((type.getId() == 'F' && (client.getFriends().size() >= (client.isSubscriber() ? 100 : 50))) || (type.getId() == 'i' && (client.getEnemies().size() >= (client.isSubscriber() ? 100 : 50)))) {
            client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.FRIENDS_LIST_FULL));
            return true;
        }

        Optional<GameClient> optionalTarget;
        if (isCatchAll) {
            optionalTarget = client.getServer().getClients().stream().filter(c -> c.getNickname().equalsIgnoreCase(name) || (c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
        } else {
            optionalTarget = client.getServer().getClients().stream().filter(isNickname ? c -> c.getNickname().equalsIgnoreCase(name) : (c -> c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
        }
        if (optionalTarget.isEmpty()) {
            client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.CANT_ADD_FRIEND_NOT_FOUND));
            return true;
        }

        target = optionalTarget.get();
        if ((type.getId() == 'F' && client.getFriends().contains(target.getAccountId())) || (type.getId() == 'i' && client.getEnemies().contains(target.getAccountId()))) {
            client.sendProtocolMessage(new FriendsListErrorMessage(type, FriendsListErrorMessage.Code.ALREADY_YOUR_FRIEND));
            return true;
        }

        switch (type) {
            case FRIENDS:
                client.getServer().getAuthDatabase().getDsl().insertInto(FRIENDS_LIST).set(FRIENDS_LIST.REQUESTER_ID, client.getAccountId()).set(FRIENDS_LIST.RECIPIENT_ID, target.getAccountId()).set(FRIENDS_LIST.TYPE, (byte) 0).execute();
                client.getFriends().add(target.getAccountId());

                client.sendProtocolMessage(this);
                client.sendProtocolMessage(new FriendsListMessage(FriendsListMessage.Type.FRIENDS, client));
                break;
            case ENEMIES:
                client.getServer().getAuthDatabase().getDsl().insertInto(FRIENDS_LIST).set(FRIENDS_LIST.REQUESTER_ID, client.getAccountId()).set(FRIENDS_LIST.RECIPIENT_ID, target.getAccountId()).set(FRIENDS_LIST.TYPE, (byte) 1).execute();
                client.getEnemies().add(target.getAccountId());

                client.sendProtocolMessage(this);
                client.sendProtocolMessage(new FriendsListMessage(FriendsListMessage.Type.ENEMIES, client));
                break;
        }
        return true;
    }
}
