package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.database.generated.auth.tables.Friends;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.protocol.server.FriendsListErrorMessage;
import lombok.Data;

import java.util.Optional;
import static fr.aquazus.diva.database.generated.auth.Tables.FRIENDS;

public @Data class FriendsListAddMessage extends ProtocolMessage {

    private boolean isCatchAll;
    private boolean isNickname;
    private String name;
    private GameClient target;

    @Override
    public String serialize() {
        return "FAK" + target.getNickname() + ';' +
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
            client.sendProtocolMessage(new FriendsListErrorMessage(FriendsListErrorMessage.Type.CANT_ADD_YOU));
            return true;
        }
        if (client.getFriends().size() >= 100) {
            client.sendProtocolMessage(new FriendsListErrorMessage(FriendsListErrorMessage.Type.FRIENDS_LIST_FULL));
            return true;
        }

        Optional<GameClient> optionalTarget;
        if (isCatchAll) {
            optionalTarget = client.getServer().getClients().stream().filter(c -> c.getNickname().equalsIgnoreCase(name) || (c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
        } else {
            optionalTarget = client.getServer().getClients().stream().filter(isNickname ? c -> c.getNickname().equalsIgnoreCase(name) : (c -> c.getCharacter() != null && c.getCharacter().getName().equalsIgnoreCase(name))).findFirst();
        }
        if (optionalTarget.isEmpty()) {
            client.sendProtocolMessage(new FriendsListErrorMessage(FriendsListErrorMessage.Type.CANT_ADD_FRIEND_NOT_FOUND));
            return true;
        }

        target = optionalTarget.get();
        if (client.getFriends().contains(target.getAccountId())) {
            client.sendProtocolMessage(new FriendsListErrorMessage(FriendsListErrorMessage.Type.ALREADY_YOUR_FRIEND));
            return true;
        }

        client.getServer().getAuthDatabase().getDsl().insertInto(FRIENDS).set(FRIENDS.REQUESTER_ID, client.getAccountId()).set(FRIENDS.RECIPIENT_ID, target.getAccountId()).execute();
        client.getFriends().add(target.getAccountId());

        client.sendProtocolMessage(this);
        client.sendProtocolMessage(new FriendsListMessage(client));

        return true;
    }
}
