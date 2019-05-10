package fr.aquazus.diva.game.protocol.client;

import fr.aquazus.diva.common.network.DivaClient;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.network.player.Character;
import fr.aquazus.diva.game.protocol.server.CharacterSelectedMessage;
import fr.aquazus.diva.game.protocol.server.CharacterStatsMessage;
import fr.aquazus.diva.game.protocol.server.ImMessage;
import fr.aquazus.diva.game.protocol.server.RestrictionsMessage;
import lombok.Data;

import java.text.SimpleDateFormat;

public @Data class CharacterSelectionMessage extends ProtocolMessage {

    private int characterId;

    @Override
    public CharacterSelectionMessage deserialize(String data) {
        if (data.length() < 3) return null;
        try {
            this.characterId = Integer.parseInt(data.substring(2));
        } catch (NumberFormatException ex) {
            return null;
        }
        return this;
    }

    @Override
    public boolean handle(DivaClient netClient, String packet) {
        GameClient client = (GameClient) netClient;
        if (deserialize(packet) == null) return false;
        Characters characterToUse = client.getServer().getAuthDatabase().getCharactersDao().fetchOneById(characterId);
        if (characterToUse == null) {
            client.disconnect("Trying to use an unknown character", "" + characterId);
            return true;
        }
        if (characterToUse.getAccountId() != client.getAccountId()) {
            client.disconnect("Trying to use someone else's character", "" + characterId);
            return true;
        }
        client.setCharacter(new Character(client, characterToUse));
        client.setState(GameClient.State.INGAME);
        client.getServer().getClients().stream().filter(c -> c.getFriends().contains(client.getAccountId())).forEach(c -> {
            if (c.isNotificationsFriends()) {
                c.sendProtocolMessage(new ImMessage("0143", client.getNickname() + " (<b><a href=\"asfunction:onHref,ShowPlayerPopupMenu," + characterToUse.getName() + "\">" + characterToUse.getName() + "</a></b>)"));
            }
        });
        client.sendProtocolMessage(new CharacterStatsMessage(client.getCharacter()));
        client.sendPacket("Rx0"); //TODO Mount XP
        client.sendProtocolMessage(new CharacterSelectedMessage(client.getCharacter()));
        /** TODO
         *  ItemSet Bonuses
         *  Gobball set (id 1) full :
         *  OS+1|2425;2416;2428;2419;2422;2411;2414|76#32#0#0,7e#32#0#0,70#5#0#0,7c#14#0#0,6f#1#0#0,6e#1e#0#0
         *
         *  Adventurer set (id 1) only ring :
         *  OS+5|2475|
         */
        client.sendPacket("ZS1"); //TODO Align order id
        client.sendPacket("cC+" + client.getChatChannels());
        /** TODO
         *  SubAreas list for worldmap
         *  al|270;0|49;1|......
         *
         *  id;align
         */
        client.sendPacket("SLo+"); //Can see next spells & use spell points
        /** TODO
         *  Spells list
         *
         *  SL164~1~b;169~1~c;161~3~d;163~1~e;165~5~f;172~3~g;167~1~h;168~1~i;162~3~j;
         *
         *  id~level~position
         */
        client.sendProtocolMessage(new RestrictionsMessage(client.getCharacter()));
        client.sendPacket("Ow0|" + client.getCharacter().getStats().getPods());
        client.sendPacket("FO" + (client.isNotificationsFriends() ? "+" : "-"));
        client.sendProtocolMessage(new ImMessage("189"));
        if (client.getLastIp() != null && client.getLastOnline() != null) {
            client.sendProtocolMessage(new ImMessage("0152", new SimpleDateFormat("yyyy~MM~dd~HH~mm~").format(client.getLastOnline()) + client.getLastIp()));
        }
        client.sendProtocolMessage(new ImMessage("0153", client.getIp()));
        client.getCharacter().startRegenTimer(2000);
        client.getCharacter().joinMap(characterToUse.getMapId(), characterToUse.getCellId());
        client.sendPacket("BT" + System.currentTimeMillis());
        client.sendPacket("fC0"); //TODO Map fight count
        return true;
    }
}
