package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

public @Data class CharacterListMessage extends ProtocolMessage {

    private int remainingSubscription;
    private int characterCount;
    private List<Characters> characterList;

    public CharacterListMessage(int remainingSubscription, int characterCount, List<Characters> characterList) {
        this.remainingSubscription = remainingSubscription;
        this.characterCount = characterCount;
        this.characterList = characterList;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("ALK");
        builder.append(remainingSubscription);
        builder.append("|");
        builder.append(characterCount);
        Iterator<Characters> iterator = characterList.iterator();
        if (iterator.hasNext()) builder.append("|");
        while (iterator.hasNext()) {
            Characters characterPojo = iterator.next();
            builder.append(characterPojo.getId()).append(";");
            builder.append(characterPojo.getName()).append(";");
            builder.append(characterPojo.getLevel()).append(";");
            builder.append(characterPojo.getGfxId()).append(";");
            builder.append(Integer.toHexString(characterPojo.getColor1())).append(";");
            builder.append(Integer.toHexString(characterPojo.getColor2())).append(";");
            builder.append(Integer.toHexString(characterPojo.getColor3())).append(";");
            builder.append(",,,,;"); //TODO cac , coiffe , cape , fami , bouclier
            builder.append(characterPojo.getIsMerchant()).append(";");
            builder.append(characterPojo.getServerId()).append(";");
            builder.append(";;"); //TODO isDead ; deathCount ; lvlMax (heroic)
            if (iterator.hasNext()) {
                builder.append("|");
            }
        }
        return builder.toString();
    }
}
