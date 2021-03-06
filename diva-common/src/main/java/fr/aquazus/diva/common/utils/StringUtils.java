package fr.aquazus.diva.common.utils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringUtils {

    private static Pattern noDigitsFormat = Pattern.compile(".*[^a-zA-Z-].*");
    private static Pattern capsPositionFormat = Pattern.compile("(?<=^.{1}).*[A-Z].*");
    private static Pattern vowelPattern = Pattern.compile("[aeiouAEIOU]+");
    private static String[] forbiddenNames = new String[] {"xelor", "iop", "feca", "eniripsa", "sadida", "ecaflip", "enutrof", "pandawa", "sram", "cra", "osamodas", "sacrieur", "drop", "mule", "admin", "ankama", "dofus", "staff", "moderateur"};

    public static boolean isValidNickname(String name) {
        if (name.length() < 3 || name.length() > 12) return false;
        if (noDigitsFormat.matcher(name).matches()) return false;
        if (name.chars().filter(ch -> ch == '-').count() > 1) return false;
        if (Arrays.stream(forbiddenNames).anyMatch(name.toLowerCase()::contains)) return false;
        if (capsPositionFormat.matcher(name).find()) return false;
        if (maxRepeatingCharCount(name) > 2) return false;
        return true;
    }

    public static boolean isValidCharacterName(String name) {
        if (name.length() < 2 || name.length() > 20) return false;
        if (name.chars().filter(ch -> ch == '-').count() > 1) return false;
        if (noDigitsFormat.matcher(name).matches()) return false;
        if (Character.isLowerCase(name.charAt(0))) return false;
        if (Arrays.stream(name.split("-")).anyMatch(s -> capsPositionFormat.matcher(s).find())) return false;
        if (Arrays.stream(forbiddenNames).anyMatch(name.toLowerCase()::contains)) return false;
        if (maxRepeatingCharCount(name) > 2) return false;
        if (!vowelPattern.matcher(name).find()) return false;
        return true;
    }

    private static int maxRepeatingCharCount(String string) {
        if (string.isEmpty()) {
            return 0;
        }
        int longest = 0;
        int length = 1;
        for (int i = 0; i < string.length() - 1; i++) {
            if (string.charAt(i) == string.charAt(i + 1)) {
                ++length;
            } else {
                if (length > longest) {
                    longest = length;
                }
                length = 1;
            }
        }
        if (length > longest) {
            longest = length;
        }
        return longest;
    }
}
