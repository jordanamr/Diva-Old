package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.maps.GameMap;

import java.time.LocalDate;

public class MapDataMessage extends ProtocolMessage {

    private GameMap map;

    public MapDataMessage(GameMap map) {
        this.map = map;
    }

    @Override
    public String serialize() {
        return "GDM|" + map.getId() + "|" + map.getDate() + (map.getKey() != null ? "|" + map.getKey() : "");
    }
}
