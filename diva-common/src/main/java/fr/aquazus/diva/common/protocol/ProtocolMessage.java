package fr.aquazus.diva.common.protocol;


public abstract class ProtocolMessage {

    public String serialize() {
        throw new UnsupportedOperationException();
    }

    public ProtocolMessage deserialize(String data) {
        throw new UnsupportedOperationException();
    }
}
