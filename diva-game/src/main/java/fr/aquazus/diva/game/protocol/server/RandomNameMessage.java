package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNameMessage extends ProtocolMessage {

    private String name;

    public RandomNameMessage() {
        this.name = generateName();
    }

    @Override
    public String serialize() {
        return "APK" + this.name;
    }

    private static final String[] prefixes = { "Kr", "Ca", "Ra", "Mrok", "Cru",
            "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
            "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
            "Mar", "Luk", "Fina", "Kharl", "Boute", "Lookie", "Daska", "Shadow",
            "One", "Eira", "Shaut", "Im", "Rak", "Tere", "Ikateur", "Acety", "Ez",
            "At", "Boum", "Nuu", "Demon", "Gale", "Toc", "Seau", "Vola", "Waiw",
            "Egmal", "Wesh" };
    private static final String[] middles = { "air", "ir", "mi", "sor", "mee", "clo",
            "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer", "Meh",
            "marac", "zoir", "slamar", "salmar", "urak",  "whit", "boul", "met", "eipan",
            "jfou", "ine", "troic", "with", "cine", "tit", "me", "zior", "Waiwa",
            "dash", "chett", "ko", "x", "dude", "arang" };
    private static final String[] suffixes = { "d", "ed", "ark", "arc", "es", "er", "der",
            "tron", "med", "ure", "zur", "cred", "mur", "lena", "flika", "feca", "tiosse",
            "don", "lifer", "X", "superbei", "homme", "wouliss", "tank", "ound", "rang",
            "ksosakai", "terpe", "jepe", "galos", "warre", "catakk", "alor" };

    private static String generateName() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String middle = middles[random.nextInt(middles.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        if (prefix.length() >= 4 || suffix.length() >= 4) {
            middle = "-";
            suffix = suffix.substring(0, 1).toUpperCase() + suffix.substring(1).toLowerCase();
        }
        return prefix + middle + suffix;
    }
}
