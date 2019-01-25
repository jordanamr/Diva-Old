package fr.aquazus.diva.game.network;

public class Character {

    private GameClient client;
    private int id;
    private String name;
    private int level;
    private int gfxId;
    private String color1, color2, color3;
    private boolean merchant;

    public Character(GameClient client, int id, String name, int level, int gfxId, int color1, int color2, int color3, boolean merchant) {
        this.client = client;
        this.id = id;
        this.name = name;
        this.level = level;
        this.gfxId = gfxId;
        this.color1 = Integer.toHexString(color1);
        this.color2 = Integer.toHexString(color2);
        this.color3 = Integer.toHexString(color3);
        this.merchant = merchant;
    }
}
