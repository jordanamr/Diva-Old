package fr.aquazus.diva.game.protocol.containers;

import lombok.Data;

public @Data class CharacterStatsContainer {



    private int[] ap, mp, range, summons;
    private int[] vitality, wisdom;
    private int[] strength, chance, agility, intelligence;

    private int initiative, prospecting;

    private int[] damageBonusFl, meleeBonusFl, masteryBonusFl, damageBonusPer;
    private int[] healBonusFl, trapBonusFl, trapBonusPer, damageReflectionFl;
    private int[] criticalBonusFl, failureBonusFl, apParryFl, mpParryFl;

    private int[] neutralResFl, neutralResPer, pvpNeutralResFl, pvpNeutralResPer;
    private int[] earthResFl, earthResPer, pvpEarthResFl, pvpEarthResPer;
    private int[] waterResFl, waterResPer, pvpWaterResFl, pvpWaterResPer;
    private int[] airResFl, airResPer, pvpAirResFl, pvpAirResPer;
    private int[] fireResFl, fireResPer, pvpFireResFl, pvpFireResPer;

    public CharacterStatsContainer() {
        ap = mp = range = summons = new int[]{0, 0, 0, 0, 0};
        vitality = wisdom = new int[]{0, 0, 0, 0, 0};
        strength = chance = agility = intelligence = new int[]{0, 0, 0, 0, 0};
        initiative = prospecting = 0;
        damageBonusFl = meleeBonusFl = masteryBonusFl = damageBonusPer = new int[]{0, 0, 0, 0, 0};
    }


    // TODO Move this to game server
    public int calculateInitiative(int hp, int hpMax, int bonus) {
        return ((strength[4] + chance[4] + agility[4] + intelligence[4]) + bonus) * (hp / hpMax);
    }
}
