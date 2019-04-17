package fr.aquazus.diva.auth.network;

import fr.aquazus.diva.common.network.DivaCipher;

import java.util.concurrent.ThreadLocalRandom;

public class AuthCipher extends DivaCipher {

    private final String ticketChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String decodePassword(String password, String key) {
        if (password.startsWith("#1")) password = password.substring(2);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < password.length(); i += 2) {
            char keyChar = key.charAt(i / 2);
            int index1 = zkArray.indexOf(password.charAt(i)) + zkArray.size();
            int index2 = zkArray.indexOf(password.charAt(i + 1)) + zkArray.size();

            int decodedIndex1 = index1 - (int) keyChar;
            if (decodedIndex1 < 0) decodedIndex1 += 64;
            decodedIndex1 *= 16;

            int decodedIndex2 = index2 - (int) keyChar;
            if (decodedIndex2 < 0) decodedIndex2 += 64;

            builder.append((char) (decodedIndex1 + decodedIndex2));
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

    private int ip2int(String ip) {
        String[] parts = ip.split("\\.");
        int iip = 0;
        for (int i = 0; i < parts.length; i++) {
            iip |= Integer.parseInt(parts[i]) << (i * 8);
        }
        return iip;
    }

    public String generateTicket() {
        StringBuilder builder = new StringBuilder(11);
        for (int i = 0; i < builder.capacity(); i++) {
            builder.append(ticketChars.charAt(ThreadLocalRandom.current().nextInt(ticketChars.length())));
        }
        return builder.toString();
    }
}
