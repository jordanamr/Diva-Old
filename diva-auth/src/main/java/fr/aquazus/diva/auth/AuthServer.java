package fr.aquazus.diva.auth;

import fr.aquazus.diva.auth.network.AuthCipher;
import fr.aquazus.diva.auth.network.AuthClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import simplenet.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AuthServer {

    private static AuthServer instance = null;

    private static AuthServer getInstance() {
        if (instance == null) instance = new AuthServer();
        return instance;
    }

    public static void main(String[] args) {
        AuthServer.getInstance().start();
    }

    public static boolean debug = true;
    private final List<AuthClient> clients;
    @Getter
    private final AuthCipher cipher;

    private AuthServer() {
        clients = Collections.synchronizedList(new ArrayList<>());
        cipher = new AuthCipher();
    }

    private void start() {
        Server netServer = new Server(2048);
        netServer.onConnect(netClient -> {
            String clientIp;
            try {
                clientIp = netClient.getChannel().getRemoteAddress().toString().substring(1).split(":")[0];
            } catch (Exception ex) {
                log.error("An error occurred while parsing an user IP", ex);
                if (netClient.getChannel().isOpen()) netClient.close();
                return;
            }
            log.info("[" + clientIp + "] connected!");
            this.clients.add(new AuthClient(this, netClient, clientIp));
        });
        netServer.bind("127.0.0.1", 4444);
    }
}
