package fr.aquazus.diva.game.network.player;

import lombok.Data;

public @Data class CharacterStats {
    private Character character;

    private int[] ap, mp, range, summons;
    private int[] vitality, wisdom;
    private int[] strength, chance, agility, intelligence;

    private int[] damageBonusFl, meleeBonusFl, masteryBonusFl, damageBonusPer;
    private int[] healBonusFl, trapBonusFl, trapBonusPer, damageReflectionFl;
    private int[] criticalBonusFl, failureBonusFl, apParryFl, mpParryFl;

    private int[] neutralResFl, neutralResPer, pvpNeutralResFl, pvpNeutralResPer;
    private int[] earthResFl, earthResPer, pvpEarthResFl, pvpEarthResPer;
    private int[] waterResFl, waterResPer, pvpWaterResFl, pvpWaterResPer;
    private int[] airResFl, airResPer, pvpAirResFl, pvpAirResPer;
    private int[] fireResFl, fireResPer, pvpFireResFl, pvpFireResPer;

    private int initiativeBonus, podsBonus, prospectingBonus;

    public CharacterStats(Character character, int[] base) {
        this.character = character;
        ap = new int[]{character.getLevel() >= 100 ? 7 : 6, 0, 0, 0, 0}; mp = new int[]{3, 0, 0, 0, 3};
        ap[4] = ap[0];
        range = new int[]{0, 0, 0, 0, 0}; summons = new int[]{1, 0, 0, 0, 1}; vitality = new int[]{base[0], 0, 0, 0, base[0]};
        wisdom = new int[]{base[1], 0, 0, 0, base[1]}; strength = new int[]{base[2], 0, 0, 0, base[2]}; chance = new int[]{base[4], 0, 0, 0, base[4]};
        agility = new int[]{base[5], 0, 0, 0, base[5]}; intelligence = new int[]{base[3], 0, 0, 0, base[3]}; damageBonusFl = new int[]{0, 0, 0, 0, 0};
        meleeBonusFl = new int[]{0, 0, 0, 0, 0}; masteryBonusFl = new int[]{0, 0, 0, 0, 0}; damageBonusPer = new int[]{0, 0, 0, 0, 0};
        healBonusFl = new int[]{0, 0, 0, 0, 0}; trapBonusFl = new int[]{0, 0, 0, 0, 0}; trapBonusPer = new int[]{0, 0, 0, 0, 0};
        damageReflectionFl = new int[]{0, 0, 0, 0, 0}; criticalBonusFl = new int[]{0, 0, 0, 0, 0}; failureBonusFl = new int[]{0, 0, 0, 0, 0};
        apParryFl = new int[]{0, 0, 0, 0, 0}; mpParryFl = new int[]{0, 0, 0, 0, 0}; neutralResFl = new int[]{0, 0, 0, 0, 0};
        neutralResPer = new int[]{0, 0, 0, 0, 0}; pvpNeutralResFl = new int[]{0, 0, 0, 0, 0}; pvpNeutralResPer = new int[]{0, 0, 0, 0, 0};
        earthResFl = new int[]{0, 0, 0, 0, 0}; earthResPer = new int[]{0, 0, 0, 0, 0}; pvpEarthResFl = new int[]{0, 0, 0, 0, 0};
        pvpEarthResPer = new int[]{0, 0, 0, 0, 0}; waterResFl = new int[]{0, 0, 0, 0, 0}; waterResPer = new int[]{0, 0, 0, 0, 0};
        pvpWaterResFl = new int[]{0, 0, 0, 0, 0}; pvpWaterResPer = new int[]{0, 0, 0, 0, 0}; airResFl = new int[]{0, 0, 0, 0, 0};
        airResPer = new int[]{0, 0, 0, 0, 0}; pvpAirResFl = new int[]{0, 0, 0, 0, 0}; pvpAirResPer = new int[]{0, 0, 0, 0, 0};
        fireResFl = new int[]{0, 0, 0, 0, 0}; fireResPer = new int[]{0, 0, 0, 0, 0}; pvpFireResFl = new int[]{0, 0, 0, 0, 0};
        pvpFireResPer = new int[]{0, 0, 0, 0, 0};
        initiativeBonus = 0; podsBonus = 0; prospectingBonus = 0;
        recalculate();
    }

    public void recalculate() {
        //TODO Recalculate depending on equipment
    }

    public int getInitiative() {
        return ((strength[4] + chance[4] + agility[4] + intelligence[4]) + initiativeBonus) * (character.getHp() / character.getMaxHp());
    }

    public int getPods() {
        int jobsTotalLvl = 0; //TODO Implement jobs
        int maxedJobs = 0;

        return 1000 + (strength[4] * 5) + (jobsTotalLvl * 5) + (maxedJobs * 1000) + podsBonus;
    }

    public int getProspecting() {
        return (character.getBreed() == 3 ? 120 : 100) + prospectingBonus + (chance[4] / 10);
    }

    public String getBaseStatsAsString() {
        return vitality[0] + "," + wisdom[0] + "," + strength[0] + "," + intelligence[0] + "," + chance[0] + "," + agility[0];
    }
}
