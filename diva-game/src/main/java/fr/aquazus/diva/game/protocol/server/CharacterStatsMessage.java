package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.database.game.ExperienceTable;
import fr.aquazus.diva.database.game.GameDatabase;
import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.player.Character;
import fr.aquazus.diva.game.network.player.CharacterStats;
import lombok.Data;

public @Data class CharacterStatsMessage extends ProtocolMessage {

    private Character character;
    private CharacterStats sc;
    private ExperienceTable experienceTable;

    public CharacterStatsMessage(Character character) {
        this.character = character;
        this.sc = character.getStats();
        this.experienceTable = GameDatabase.getInstance().getExperienceTable();
    }

    @Override
    public String serialize() {
        StringBuilder builder = new StringBuilder("As");

        builder.append(character.getXp()).append(','); //xp
        builder.append(experienceTable.getCharacterCap(character.getLevel())).append(','); //xpMin
        builder.append(experienceTable.getCharacterCap(character.getLevel() + 1)).append('|'); //xpMax

        builder.append(character.getKamas()).append('|'); //kamas
        builder.append(character.getCapitalStats()).append('|'); //statsPoints
        builder.append(character.getCapitalSpells()).append('|'); //spellPoints

        builder.append(character.getAlignId()).append('~').append(character.getAlignId()).append(','); //alignId~fakeAlignId
        builder.append(character.getAlignLevel()).append(','); //alignLevel
        builder.append(character.getAlignRank()).append(','); //alignRank
        builder.append(character.getAlignHonor()).append(','); //alignHonor
        builder.append(character.getAlignDishonor()).append(','); //alignDishonor
        builder.append(character.isWingsEnabled()).append('|'); //alignWings

        builder.append(character.getHp()).append(','); //hp
        builder.append(character.getMaxHp()).append('|'); //maxHp

        builder.append(character.getEnergy()).append(','); //energy
        builder.append(10000).append('|'); //maxEnergy

        builder.append(sc.getInitiative()).append('|'); //initiative
        builder.append(sc.getProspecting()).append('|'); //prospecting

        //base, equipment, gift, buff, (PA/PM: total)   |
        builder.append(sc.getAp()[0]).append(',').append(sc.getAp()[1]).append(',').append(sc.getAp()[2]).append(',').append(sc.getAp()[3]).append(',').append(sc.getAp()[4]).append('|');
        builder.append(sc.getMp()[0]).append(',').append(sc.getMp()[1]).append(',').append(sc.getMp()[2]).append(',').append(sc.getMp()[3]).append(',').append(sc.getMp()[4]).append('|');
        builder.append(sc.getStrength()[0]).append(',').append(sc.getStrength()[1]).append(',').append(sc.getStrength()[2]).append(',').append(sc.getStrength()[3]).append('|');
        builder.append(sc.getVitality()[0]).append(',').append(sc.getVitality()[1]).append(',').append(sc.getVitality()[2]).append(',').append(sc.getVitality()[3]).append('|');
        builder.append(sc.getWisdom()[0]).append(',').append(sc.getWisdom()[1]).append(',').append(sc.getWisdom()[2]).append(',').append(sc.getWisdom()[3]).append('|');
        builder.append(sc.getChance()[0]).append(',').append(sc.getChance()[1]).append(',').append(sc.getChance()[2]).append(',').append(sc.getChance()[3]).append('|');
        builder.append(sc.getAgility()[0]).append(',').append(sc.getAgility()[1]).append(',').append(sc.getAgility()[2]).append(',').append(sc.getAgility()[3]).append('|');
        builder.append(sc.getIntelligence()[0]).append(',').append(sc.getIntelligence()[1]).append(',').append(sc.getIntelligence()[2]).append(',').append(sc.getIntelligence()[3]).append('|');
        builder.append(sc.getRange()[0]).append(',').append(sc.getRange()[1]).append(',').append(sc.getRange()[2]).append(',').append(sc.getRange()[3]).append('|');
        builder.append(sc.getSummons()[0]).append(',').append(sc.getSummons()[1]).append(',').append(sc.getSummons()[2]).append(',').append(sc.getSummons()[3]).append('|');

        builder.append(sc.getDamageBonusFl()[0]).append(',').append(sc.getDamageBonusFl()[1]).append(',').append(sc.getDamageBonusFl()[2]).append(',').append(sc.getDamageBonusFl()[3]).append('|');
        builder.append(sc.getMeleeBonusFl()[0]).append(',').append(sc.getMeleeBonusFl()[1]).append(',').append(sc.getMeleeBonusFl()[2]).append(',').append(sc.getMeleeBonusFl()[3]).append('|');
        builder.append(sc.getMasteryBonusFl()[0]).append(',').append(sc.getMasteryBonusFl()[1]).append(',').append(sc.getMasteryBonusFl()[2]).append(',').append(sc.getMasteryBonusFl()[3]).append('|');
        builder.append(sc.getDamageBonusPer()[0]).append(',').append(sc.getDamageBonusPer()[1]).append(',').append(sc.getDamageBonusPer()[2]).append(',').append(sc.getDamageBonusPer()[3]).append('|');
        builder.append(sc.getHealBonusFl()[0]).append(',').append(sc.getHealBonusFl()[1]).append(',').append(sc.getHealBonusFl()[2]).append(',').append(sc.getHealBonusFl()[3]).append('|');
        builder.append(sc.getTrapBonusFl()[0]).append(',').append(sc.getTrapBonusFl()[1]).append(',').append(sc.getTrapBonusFl()[2]).append(',').append(sc.getTrapBonusFl()[3]).append('|');
        builder.append(sc.getTrapBonusPer()[0]).append(',').append(sc.getTrapBonusPer()[1]).append(',').append(sc.getTrapBonusPer()[2]).append(',').append(sc.getTrapBonusPer()[3]).append('|');
        builder.append(sc.getDamageReflectionFl()[0]).append(',').append(sc.getDamageReflectionFl()[1]).append(',').append(sc.getDamageReflectionFl()[2]).append(',').append(sc.getDamageReflectionFl()[3]).append('|');
        builder.append(sc.getCriticalBonusFl()[0]).append(',').append(sc.getCriticalBonusFl()[1]).append(',').append(sc.getCriticalBonusFl()[2]).append(',').append(sc.getCriticalBonusFl()[3]).append('|');
        builder.append(sc.getFailureBonusFl()[0]).append(',').append(sc.getFailureBonusFl()[1]).append(',').append(sc.getFailureBonusFl()[2]).append(',').append(sc.getFailureBonusFl()[3]).append('|');
        builder.append(sc.getApParryFl()[0]).append(',').append(sc.getApParryFl()[1]).append(',').append(sc.getApParryFl()[2]).append(',').append(sc.getApParryFl()[3]).append('|');
        builder.append(sc.getMpParryFl()[0]).append(',').append(sc.getMpParryFl()[1]).append(',').append(sc.getMpParryFl()[2]).append(',').append(sc.getMpParryFl()[3]).append('|');

        builder.append(sc.getNeutralResFl()[0]).append(',').append(sc.getNeutralResFl()[1]).append(',').append(sc.getNeutralResFl()[2]).append(',').append(sc.getNeutralResFl()[3]).append('|');
        builder.append(sc.getNeutralResPer()[0]).append(',').append(sc.getNeutralResPer()[1]).append(',').append(sc.getNeutralResPer()[2]).append(',').append(sc.getNeutralResPer()[3]).append('|');
        builder.append(sc.getPvpNeutralResFl()[0]).append(',').append(sc.getPvpNeutralResFl()[1]).append(',').append(sc.getPvpNeutralResFl()[2]).append(',').append(sc.getPvpNeutralResFl()[3]).append('|');
        builder.append(sc.getPvpNeutralResPer()[0]).append(',').append(sc.getPvpNeutralResPer()[1]).append(',').append(sc.getPvpNeutralResPer()[2]).append(',').append(sc.getPvpNeutralResPer()[3]).append('|');
        builder.append(sc.getEarthResFl()[0]).append(',').append(sc.getEarthResFl()[1]).append(',').append(sc.getEarthResFl()[2]).append(',').append(sc.getEarthResFl()[3]).append('|');
        builder.append(sc.getEarthResPer()[0]).append(',').append(sc.getEarthResPer()[1]).append(',').append(sc.getEarthResPer()[2]).append(',').append(sc.getEarthResPer()[3]).append('|');
        builder.append(sc.getPvpEarthResFl()[0]).append(',').append(sc.getPvpEarthResFl()[1]).append(',').append(sc.getPvpEarthResFl()[2]).append(',').append(sc.getPvpEarthResFl()[3]).append('|');
        builder.append(sc.getPvpEarthResPer()[0]).append(',').append(sc.getPvpEarthResPer()[1]).append(',').append(sc.getPvpEarthResPer()[2]).append(',').append(sc.getPvpEarthResPer()[3]).append('|');
        builder.append(sc.getWaterResFl()[0]).append(',').append(sc.getWaterResFl()[1]).append(',').append(sc.getWaterResFl()[2]).append(',').append(sc.getWaterResFl()[3]).append('|');
        builder.append(sc.getWaterResPer()[0]).append(',').append(sc.getWaterResPer()[1]).append(',').append(sc.getWaterResPer()[2]).append(',').append(sc.getWaterResPer()[3]).append('|');
        builder.append(sc.getPvpWaterResFl()[0]).append(',').append(sc.getPvpWaterResFl()[1]).append(',').append(sc.getPvpWaterResFl()[2]).append(',').append(sc.getPvpWaterResFl()[3]).append('|');
        builder.append(sc.getPvpWaterResPer()[0]).append(',').append(sc.getPvpWaterResPer()[1]).append(',').append(sc.getPvpWaterResPer()[2]).append(',').append(sc.getPvpWaterResPer()[3]).append('|');
        builder.append(sc.getAirResFl()[0]).append(',').append(sc.getAirResFl()[1]).append(',').append(sc.getAirResFl()[2]).append(',').append(sc.getAirResFl()[3]).append('|');
        builder.append(sc.getAirResPer()[0]).append(',').append(sc.getAirResPer()[1]).append(',').append(sc.getAirResPer()[2]).append(',').append(sc.getAirResPer()[3]).append('|');
        builder.append(sc.getPvpAirResFl()[0]).append(',').append(sc.getPvpAirResFl()[1]).append(',').append(sc.getPvpAirResFl()[2]).append(',').append(sc.getPvpAirResFl()[3]).append('|');
        builder.append(sc.getPvpAirResPer()[0]).append(',').append(sc.getPvpAirResPer()[1]).append(',').append(sc.getPvpAirResPer()[2]).append(',').append(sc.getPvpAirResPer()[3]).append('|');
        builder.append(sc.getFireResFl()[0]).append(',').append(sc.getFireResFl()[1]).append(',').append(sc.getFireResFl()[2]).append(',').append(sc.getFireResFl()[3]).append('|');
        builder.append(sc.getFireResPer()[0]).append(',').append(sc.getFireResPer()[1]).append(',').append(sc.getFireResPer()[2]).append(',').append(sc.getFireResPer()[3]).append('|');
        builder.append(sc.getPvpFireResFl()[0]).append(',').append(sc.getPvpFireResFl()[1]).append(',').append(sc.getPvpFireResFl()[2]).append(',').append(sc.getPvpFireResFl()[3]).append('|');
        builder.append(sc.getPvpFireResPer()[0]).append(',').append(sc.getPvpFireResPer()[1]).append(',').append(sc.getPvpFireResPer()[2]).append(',').append(sc.getPvpFireResPer()[3]).append('|');

        return builder.toString();
    }
}
