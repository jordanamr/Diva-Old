package fr.aquazus.diva.game.network.maps;

import fr.aquazus.diva.game.GameServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapsManager {

    private GameServer server;
    private Map<Integer, GameMap> loadedMaps;

    public MapsManager(GameServer server) {
        this.server = server;
        this.loadedMaps = Collections.synchronizedMap(new HashMap<>());
    }

    public GameMap getMap(int id) {
        if (loadedMaps.containsKey(id)) {
            return loadedMaps.get(id);
        }
        String[] mapData = server.getGameDatabase().getMapsData().get(id);
        if (mapData != null && mapData.length > 1) {
            GameMap map = new GameMap(id, mapData[0], Integer.parseInt(mapData[1]), Integer.parseInt(mapData[2]), mapData[3]);
            loadedMaps.put(id, map);
            return map;
        }
        return null;
    }

}
