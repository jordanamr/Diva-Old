package fr.aquazus.diva.game.network.player;

import lombok.Data;

public @Data class CharacterRestrictions {

    private int value;
    private boolean canGetAssaulted, canGetChallenged, canTrade;
    private boolean canGetAttacked, forcedWalk, isSlow;
    private boolean canSwitchToCreature, isTomb, canAttack;
    private boolean canChat, canSwitchToMerchant, canUseItems;
    private boolean canInteractWithCollectors, canUseInteractives;
    private boolean canTalkToNPCs, canAttackDungeonMonstersAsMutant;
    private boolean canMoveInAllAxis, canInteractWithPrisms, canAttackMonstersAsMutant;

    public CharacterRestrictions(int value) {
        this.value = value;
        recalculateFromValue();
    }

    public void recalculateFromValue() {
        recalculateFromValue(this.value);
    }

    public void recalculateFromValue(int value) {
        this.value = value;
        canGetAssaulted = ((value & 1) != 1);
        canGetChallenged = ((value & 2) != 2);
        canTrade = ((value & 4) != 4);
        canGetAttacked = ((value & 8) != 8);
        forcedWalk = ((value & 16) == 16);
        isSlow = ((value & 32) == 32);
        canSwitchToCreature = ((value & 64) != 64);
        isTomb = ((value & 128) == 128);
        canAttack = ((value & 8) == 8);
        canChat = ((value & 16) != 16);
        canSwitchToMerchant = ((value & 32) != 32);
        canUseItems = ((value & 64) != 64);
        canInteractWithCollectors = ((value & 128) != 128);
        canUseInteractives = ((value & 256) != 256);
        canTalkToNPCs = ((value & 512) != 512);
        canAttackDungeonMonstersAsMutant = ((value & 4096) == 4096);
        canMoveInAllAxis = ((value & 8192) == 8192);
        canAttackMonstersAsMutant = ((value & 16384) == 16384);
        canInteractWithPrisms = ((value & 32768) != 32768);
    }

    public void recalculateFromFlags() {
        int newValue = 0;
        if (!canInteractWithPrisms) newValue += 32768;
        if (canAttackMonstersAsMutant) newValue += 16384;
        if (canMoveInAllAxis) newValue += 8192;
        if (canAttackDungeonMonstersAsMutant) newValue += 4096;
        if (!canTalkToNPCs) newValue += 512;
        if (!canUseInteractives) newValue += 256;
        if (!canInteractWithCollectors) newValue += 128;
        if (!canUseItems) newValue += 64;
        if (!canSwitchToMerchant) newValue += 32;
        if (!canChat) newValue += 16;
        if (canAttack) newValue += 8;
        if (!canGetAttacked) newValue += 8;
        if (!canTrade) newValue += 4;
        if (!canGetChallenged) newValue += 2;
        if (!canGetAssaulted) newValue += 1;
        this.value = newValue;
    }

}
