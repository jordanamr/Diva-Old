package fr.aquazus.diva.protocol.auth.server;

import fr.aquazus.diva.protocol.ProtocolMessage;
import lombok.Data;

public @Data class AuthAddressMessage extends ProtocolMessage {

    private String address;
    private String ticket;

    public AuthAddressMessage(String address, String ticket) {
        this.address = address;
        this.ticket = ticket;
    }

    @Override
    public String serialize() {
        return "AXK" + address + ticket;
    }
}
