package fr.aquazus.diva.game.network.player;

import fr.aquazus.diva.database.generated.auth.tables.pojos.Characters;
import fr.aquazus.diva.game.network.GameClient;
import fr.aquazus.diva.game.network.maps.GameMap;
import fr.aquazus.diva.game.protocol.server.GameMovementMessage;
import fr.aquazus.diva.game.protocol.server.MapDataMessage;
import lombok.Data;

import java.util.Arrays;

import static fr.aquazus.diva.database.generated.auth.Tables.CHARACTERS;

public @Data class Character {

    private int id;
    private GameClient client;
    private String name;
    private int breed, gender;
    private int level, kamas;
    private short capitalStats, capitalSpells;
    private long xp;
    private short gfxId;
    private String color1, color2, color3;

    private byte alignId, alignLevel, alignRank;
    private short alignHonor, alignDishonor;
    private boolean wingsEnabled;

    private int hp;
    private short energy;

    private CharacterStats stats;
    private CharacterRestrictions restrictions;

    private GameMap currentMap;
    private int cellId;

    public Character(GameClient client, Characters pojo) {
        this.client = client;
        this.id = pojo.getId();
        this.name = pojo.getName();
        this.breed = pojo.getBreed();
        this.gender = pojo.getGender();
        this.level = pojo.getLevel();
        this.xp = pojo.getXp();
        this.kamas = pojo.getKamas();
        this.gfxId = pojo.getGfxId();
        this.capitalStats = pojo.getCapitalStats();
        this.capitalSpells = pojo.getCapitalSpells();
        this.alignId = pojo.getAlignId();
        this.alignLevel = pojo.getAlignLevel();
        this.alignRank = pojo.getAlignRank();
        this.alignHonor = pojo.getAlignHonor();
        this.alignDishonor = pojo.getAlignDishonor();
        this.wingsEnabled = pojo.getAlignWings() == (byte) 1;
        this.color1 = Integer.toHexString(pojo.getColor1());
        this.color2 = Integer.toHexString(pojo.getColor2());
        this.color3 = Integer.toHexString(pojo.getColor3());

        this.hp = pojo.getHp();
        this.energy = pojo.getEnergy();

        this.stats = new CharacterStats(this, Arrays.stream(pojo.getBaseStats().split(",")).map(Integer::parseInt).mapToInt(Integer::intValue).toArray());
        this.restrictions = new CharacterRestrictions(pojo.getRestrictions());
    }

    public int getMaxHp() {
        return 55 + ((level - 1) * 5) + stats.getVitality()[4];
    }

    public void save() {
        client.getServer().getAuthDatabase().getDsl().update(CHARACTERS).set(CHARACTERS.LEVEL, level).set(CHARACTERS.XP, xp)
                .set(CHARACTERS.KAMAS, kamas).set(CHARACTERS.GFX_ID, gfxId).set(CHARACTERS.CAPITAL_STATS, capitalStats)
                .set(CHARACTERS.CAPITAL_SPELLS, capitalSpells).set(CHARACTERS.ALIGN_ID, alignId).set(CHARACTERS.ALIGN_LEVEL, alignLevel)
                .set(CHARACTERS.ALIGN_RANK, alignRank).set(CHARACTERS.ALIGN_HONOR, alignHonor).set(CHARACTERS.ALIGN_DISHONOR, alignDishonor)
                .set(CHARACTERS.ALIGN_WINGS, (byte) (wingsEnabled ? 1 : 0)).set(CHARACTERS.HP, hp).set(CHARACTERS.ENERGY, energy)
                .set(CHARACTERS.BASE_STATS, stats.getBaseStatsAsString()).set(CHARACTERS.CELL_ID, cellId).where(CHARACTERS.ID.eq(this.id)).execute();
    }

    public void joinMap(int mapId, int cellId) {
        leaveCurrentMap();
        currentMap = client.getServer().getMapsManager().getMap(mapId);
        client.sendProtocolMessage(new MapDataMessage(currentMap));
        this.cellId = cellId;
        currentMap.addCharacter(this);
    }

    public void getMapInformations() {
        client.sendProtocolMessage(new GameMovementMessage(currentMap, GameMovementMessage.Action.ADD, currentMap.getCharactersOnMap().toArray(new Character[0])));
    }

    public void leaveCurrentMap() {
        if (this.currentMap != null) {
            currentMap.removeCharacter(this);
            this.currentMap = null;
        }
    }

    public void broadcastMovement(String path) {
        if (this.currentMap == null) return;
        //TODO Null path = invalid request
        if (path.contains("|")) {
            String[] pathData = path.split("\\|");
            currentMap.moveCharacter(this, pathData[1]);
            this.cellId = Integer.parseInt(pathData[0].split("=")[0]);
        } else {
            currentMap.moveCharacter(this, path);
        }
    }

    public void sendMessage(String channel, String message) {
        if (channel.equals("K") && currentMap != null) {
            currentMap.sendMessage(this, message);
        }
    }

    public void sendSmiley(int id) {
        if (currentMap != null) {
            currentMap.sendSmiley(this, id);
        }
    }
}
