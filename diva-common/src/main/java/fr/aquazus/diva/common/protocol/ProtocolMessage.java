package fr.aquazus.diva.common.protocol;


import fr.aquazus.diva.common.network.DivaClient;

public abstract class ProtocolMessage {

    public String serialize() {
        throw new UnsupportedOperationException();
    }

    public ProtocolMessage deserialize(String data) {
        throw new UnsupportedOperationException();
    }

    public boolean handle(DivaClient netClient, String packet) {
        throw new UnsupportedOperationException();
    }
}
