package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaCipher;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameCipher extends DivaCipher {
    private static GameCipher instance = null;

    public static GameCipher getInstance() {
        if (instance == null) instance = new GameCipher();
        return instance;
    }

    public LinkedHashMap<Integer, Integer> extractFullPath(String compressedData, int mapSize) {
        LinkedHashMap<Integer, Integer> fullPath = new LinkedHashMap<>();
        char[] data = compressedData.toCharArray();
        for (int i = 0; i < data.length; i = i + 3) {
            int cellId = (decode64((data[i + 1])) & 15) << 6 | decode64(data[i + 2]);
            int direction = decode64(data[i]);
            if (cellId < 0 || cellId > mapSize) return null;
            fullPath.put(cellId, direction);
        }
        return fullPath;
    }

    public String compressFullPath(LinkedHashMap<Integer, Integer> fullPath) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Integer> path : fullPath.entrySet()) {
            builder.append(encode64(path.getValue() & 7));
            builder.append(encode64((path.getKey() & 4032) >> 6));
            builder.append(encode64(path.getKey() & 63));
        }
        return builder.toString();
    }

}
