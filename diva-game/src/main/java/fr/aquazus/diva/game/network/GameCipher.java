package fr.aquazus.diva.game.network;

import fr.aquazus.diva.common.network.DivaCipher;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class GameCipher {

    public static LinkedHashMap<Integer, Integer> extractFullPath(String compressedData, int mapSize) {
        LinkedHashMap<Integer, Integer> fullPath = new LinkedHashMap<>();
        char[] data = compressedData.toCharArray();
        for (int i = 0; i < data.length; i = i + 3) {
            int cellId = (DivaCipher.decode64((data[i + 1])) & 15) << 6 | DivaCipher.decode64(data[i + 2]);
            int direction = DivaCipher.decode64(data[i]);
            if (cellId < 0 || cellId > mapSize) return null;
            fullPath.put(cellId, direction);
        }
        log.debug(fullPath.toString());
        return fullPath;
    }

    public static String compressFullPath(LinkedHashMap<Integer, Integer> fullPath) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Integer> path : fullPath.entrySet()) {
            builder.append(DivaCipher.encode64(path.getValue() & 7));
            builder.append(DivaCipher.encode64((path.getKey() & 4032) >> 6));
            builder.append(DivaCipher.encode64(path.getKey() & 63));
        }
        return builder.toString();
    }

}
