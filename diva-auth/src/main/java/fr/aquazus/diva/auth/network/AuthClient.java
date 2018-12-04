package fr.aquazus.diva.auth.network;

import lombok.Getter;
import simplenet.Client;

public class AuthClient {

    private Client netClient;
    @Getter
    private String ip;

    public AuthClient(Client netClient, String ip) {
        this.netClient = netClient;
        this.ip = ip;
    }
}
