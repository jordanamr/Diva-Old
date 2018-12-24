package fr.aquazus.diva.protocol;


public abstract class ProtocolMessage {

    public String serialize() {
        throw new UnsupportedOperationException();
    }

    public ProtocolMessage deserialize(String data) {
        throw new UnsupportedOperationException();
    }
}
