package fr.aquazus.diva.protocol.game.server;

import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

public @Data class CharacterStatsMessage extends ProtocolMessage {

    private Characters character;

    public CharacterStatsMessage(Characters character) {
        this.character = character;
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("As");
        builder.append("0").append(","); //xp
        builder.append("0").append(","); //xpMin
        builder.append("110").append("|"); //xpMax

        builder.append("0").append("|"); //kamas
        builder.append("0").append("|"); //statsPoints
        builder.append("0").append("|"); //spellPoints

        builder.append("0").append("~").append("0").append(","); //alignId
        builder.append("0").append(","); //alignLevel
        builder.append("0").append(","); //alignRank
        builder.append("0").append(","); //alignHonor
        builder.append("0").append(","); //alignDishonor
        builder.append("0").append("|"); //alignWings

        builder.append("55").append(","); //hp
        builder.append("55").append("|"); //maxHp

        builder.append("10000").append(","); //energy
        builder.append("10000").append("|"); //maxEnergy

        builder.append("0").append("|"); //initiative

        builder.append("100").append("|"); //prospect

        //base, equipment, gift, buff, (PA/PM: total)


        return builder.toString();
    }
}
