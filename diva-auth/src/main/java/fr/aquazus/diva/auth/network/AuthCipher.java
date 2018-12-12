package fr.aquazus.diva.auth.network;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthCipher {

    private final List<Character> zkArray = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_');

    public String decodePassword(String password, String key) {
        if (password.startsWith("#1")) password = password.substring(2);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < password.length(); i += 2) {
            int kChar = (int) key.charAt((i / 2));
            int j = (64 + zkArray.indexOf(password.charAt(i))) - kChar;
            int k = (64 + zkArray.indexOf(password.charAt(i + 1))) - kChar;
            if (k < 0) k += 64;
            builder.append((char) (16 * j + k));
        }
        return builder.toString();
    }

    public String encodeAXK(String ipPort) {
        int index = ipPort.indexOf(':');
        int ip = ip2int(ipPort.substring(0, index));
        int port = Integer.parseInt(ipPort.substring(index + 1));

        char[] obfIp = new char[8];
        for (int i = 0; i < 8; i++) {
            int pos = 4 * i;
            obfIp[i % 2 == 0 ? i + 1 : i - 1] = (char) (((ip >> pos) & 15) + 48);
        }

        char[] obfPort = new char[3];
        for (int i = 0; i < 3; i++) {
            int pos = 6 * (2 - i);
            obfPort[i] = encode64((port >> pos) & 63);
        }

        return new String(obfIp) + new String(obfPort);
    }

    private String int2ip(int ip) {
        return IntStream.of(
                ip >> 24 & 0xff,
                ip >> 16 & 0xff,
                ip >> 8 & 0xff,
                ip & 0xff)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining("."));
    }

    private int ip2int(String ip) {
        String[] parts = ip.split("\\.");
        int iip = 0;
        for (int i = 0; i < parts.length; i++) {
            iip |= Integer.parseInt(parts[i]) << (i * 8);
        }
        return iip;
    }

    private int decode64(char data) {
        return zkArray.indexOf(data);
    }

    private char encode64(int data) {
        return zkArray.get(data);
    }
}
