package fr.aquazus.diva.protocol;

public interface ProtocolHandler {
    String version = "1.29.1";
    boolean handlePacket(String packet);
}
