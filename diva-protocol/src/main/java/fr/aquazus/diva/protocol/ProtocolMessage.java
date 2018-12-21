package fr.aquazus.diva.protocol;


public abstract class ProtocolMessage {

    public String serialize() {
        throw new UnsupportedOperationException();
    }

    public void deserialize(String data) {
        throw new UnsupportedOperationException();
    }
}
